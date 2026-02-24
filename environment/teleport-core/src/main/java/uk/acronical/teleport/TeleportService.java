package uk.acronical.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.StringUtils;
import uk.acronical.task.TaskManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service responsible for orchestrating player teleportation requests.
 * <p>
 * This service manages warm-up delays, movement-based cancellations, and
 * callback execution, utilising the {@link TaskManager} for scheduled operations.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class TeleportService implements Listener {

    private final Plugin plugin;
    private final TaskManager taskManager;

    private final Map<UUID, Integer> activeWarmups = new ConcurrentHashMap<>();
    private final Map<UUID, TeleportRequest> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link TeleportService}.
     *
     * @param plugin      The plugin instance.
     * @param taskManager The {@link TaskManager} used for scheduling warm-ups.
     */
    public TeleportService(@NotNull Plugin plugin, @NotNull TaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;
    }

    /**
     * Processes a {@link TeleportRequest}.
     * <p>
     * If a delay is specified, a warm-up task is scheduled. Any existing
     * warm-ups for the player will be cancelled before the new request begins.
     *
     * @param request The teleportation parameters and callbacks.
     */
    public void teleport(@NotNull TeleportRequest request) {
        Player player = request.getPlayer();
        UUID uuid = player.getUniqueId();

        cancelActive(uuid);

        if (request.getDelay() <= 0) {
            executeTeleport(request);
            return;
        }

        player.sendMessage(StringUtils.colour(""));

        pendingRequests.put(uuid, request);

        int taskId = taskManager.sync(() -> {
            if (activeWarmups.containsKey(uuid)) {
                activeWarmups.remove(uuid);
                pendingRequests.remove(uuid);
                executeTeleport(request);
            }
        }, request.getDelay() * 20L).getTaskId();

        activeWarmups.put(uuid, taskId);
    }

    /**
     * Finalises the teleportation and triggers associated callbacks.
     */
    private void executeTeleport(TeleportRequest request) {
        Player p = request.getPlayer();

        boolean success = p.teleport(request.getDestination());

        if (success) {
            if (request.getOnSuccess() != null) {
                request.getOnSuccess().accept(p);
            }
        } else {
            p.sendMessage("§cTeleportation failed. The destination might be unsafe.");
            if (request.getOnCancel() != null) {
                request.getOnCancel().accept(p);
            }
        }
    }

    /**
     * Safely cancels an active warm-up and triggers the cancellation callback.
     *
     * @param uuid The player's unique identifier.
     */
    private void cancelActive(UUID uuid) {
        if (activeWarmups.containsKey(uuid)) {
            taskManager.cancel(activeWarmups.remove(uuid));
            TeleportRequest req = pendingRequests.remove(uuid);

            if (req != null && req.getOnCancel() != null) req.getOnCancel().accept(req.getPlayer());
        }
    }

    /**
     * Monitors player movement to determine if a warm-up should be aborted.
     */
    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ() && from.getBlockY() == to.getBlockY())) return;

        UUID uuid = event.getPlayer().getUniqueId();
        if (!activeWarmups.containsKey(uuid)) return;

        TeleportRequest req = pendingRequests.get(uuid);
        if (req != null && req.isCancelOnMove()) {
            cancelActive(uuid);
            event.getPlayer().sendMessage(StringUtils.colour("&cTeleportation cancelled because you moved."));
        }
    }

    /**
     * Cancels the event if the player leaves.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        cancelActive(event.getPlayer().getUniqueId());
    }
}
