package uk.acronical.punishment;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * A central service for issuing, revoking, and enforcing player punishments.
 * <p>
 * This service utilises an internal cache of active punishments to ensure
 * high-performance checks during login and chat events. Expired records are
 * automatically pruned via a background task.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class PunishmentService implements Listener {

    private final Plugin plugin;

    private final Map<UUID, List<Punishment>> activePunishments = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link PunishmentService} and starts the expiry cleanup task.
     *
     * @param plugin The plugin instance.
     */
    public PunishmentService(@NotNull Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            activePunishments.forEach((uuid, list) -> {
                list.removeIf(Punishment::isExpired);
                if (list.isEmpty()) {
                    activePunishments.remove(uuid);
                }
            });
        }, 12000L, 12000L);
    }

    /**
     * Issues a new punishment and enforces it immediately if the target is online.
     *
     * @param punishment The {@link Punishment} record to issue.
     * @return The issued punishment.
     */
    @NotNull
    public Punishment issuePunishment(@NotNull Punishment punishment) {
        UUID target = punishment.getTarget();
        activePunishments.computeIfAbsent(target, k -> new CopyOnWriteArrayList<>()).add(punishment);

        Player onlineTarget = Bukkit.getPlayer(target);
        if (onlineTarget != null && onlineTarget.isOnline()) enforceLivePunishment(onlineTarget, punishment);

        return punishment;
    }

    /**
     * Revokes the most recent active punishment of a specific type for a player.
     *
     * @param target The player's {@link UUID}.
     * @param type   The {@link PunishmentType} to lift.
     * @return The revoked punishment, or {@code null} if none were found.
     */
    @Nullable
    public Punishment revokePunishment(@NotNull UUID target, @NotNull PunishmentType type) {
        List<Punishment> punishments = activePunishments.get(target);
        if (punishments == null || punishments.isEmpty()) return null;

        Punishment toRemove = punishments.stream().filter(p -> p.getType() == type).filter(p -> !p.isExpired()).findFirst().orElse(null);

        if (toRemove != null) {
            punishments.remove(toRemove);

            if (punishments.isEmpty()) activePunishments.remove(target, punishments);

            Player player = Bukkit.getPlayer(target);
            if (player != null && player.isOnline() && type == PunishmentType.MUTE) player.sendMessage(StringUtils.colour("&aYour mute has been lifted."));
        }

        return toRemove;
    }

    /**
     * Retrieves a list of active (non-expired) punishments of a specific type.
     */
    @NotNull
    public List<Punishment> getActivePunishments(@NotNull UUID target, @NotNull PunishmentType type) {
        return activePunishments.getOrDefault(target, new ArrayList<>()).stream().filter(p -> p.getType() == type).filter(p -> !p.isExpired()).collect(Collectors.toList());
    }

    /**
     * Caches a list of punishments, typically called during session loading.
     */
    public void cachePunishments(@NotNull UUID target, @NotNull List<Punishment> punishments) {
        activePunishments.put(target, new CopyOnWriteArrayList<>(punishments));
    }

    /**
     * Applies immediate effects for punishments issued while the player is connected.
     */
    private void enforceLivePunishment(@NotNull Player player, @NotNull Punishment punishment) {
        switch (punishment.getType()) {
            case BAN -> Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(StringUtils.colour("&cYou have been banned.\nReason: &f" + punishment.getReason())));
            case MUTE -> player.sendMessage(StringUtils.colour("&cYou have been muted. Reason: &f" + punishment.getReason()));
            case WARN -> player.sendMessage(StringUtils.colour("&4[WARNING] &c" + punishment.getReason()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        List<Punishment> bans = getActivePunishments(uuid, PunishmentType.BAN);

        if (!bans.isEmpty()) {
            Punishment activeBan = bans.getFirst();
            String kickMessage = "&cYou are banned from this server.\n\n" + "&7Reason: &f" + activeBan.getReason() + "\n" + (activeBan.getExpiryTime() == -1 ? "&4This ban is permanent." : "&7Expires: &f" + new java.util.Date(activeBan.getExpiryTime()));
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, StringUtils.colour(kickMessage));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        List<Punishment> mutes = getActivePunishments(player.getUniqueId(), PunishmentType.MUTE);

        if (!mutes.isEmpty()) {
            event.setCancelled(true);
            Punishment activeMute = mutes.getFirst();
            player.sendMessage(StringUtils.colour("&cYou cannot speak. You are currently muted for: &f" + activeMute.getReason()));
        }
    }
}
