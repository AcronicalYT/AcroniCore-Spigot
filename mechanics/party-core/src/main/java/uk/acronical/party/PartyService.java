package uk.acronical.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A central service for managing party lifecycles and player associations.
 * <p>
 * This service facilitates the creation, joining, and disbanding of groups,
 * ensuring that player states are synchronised and cleaned up upon disconnection.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class PartyService implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Party> playerParties = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link PartyService}.
     *
     * @param plugin The plugin instance.
     */
    public PartyService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new party with the specified leader.
     *
     * @param leader The player to become the initial leader.
     * @return The newly created {@link Party}.
     * @throws IllegalStateException If the player is already associated with a party.
     */
    @NotNull
    public Party createParty(@NotNull Player leader) {
        UUID uuid = leader.getUniqueId();
        if (playerParties.containsKey(uuid)) throw new IllegalStateException("Player is already in a party");

        Party party = new Party(uuid);
        playerParties.put(uuid, party);
        return party;
    }

    @Nullable
    public Party getParty(Player player) {
        return playerParties.get(player.getUniqueId());
    }

    public boolean hasParty(Player player) {
        return playerParties.containsKey(player.getUniqueId());
    }

    /**
     * Verifies if two players are currently members of the same party.
     *
     * @param playerA The first player.
     * @param playerB The second player.
     * @return {@code true} if both players share the same party instance.
     */
    public boolean areInSameParty(@NotNull Player playerA, @NotNull Player playerB) {
        Party partyA = getParty(playerA);
        if (partyA == null) return false;

        Party partyB = getParty(playerB);
        return partyA.equals(partyB);
    }

    /**
     * Adds a player to an existing party and updates the registry.
     *
     * @param player The player to join.
     * @param party  The {@link Party} to join.
     */
    public void joinParty(@NotNull Player player, @NotNull Party party) {
        UUID uuid = player.getUniqueId();
        if (hasParty(player)) return;

        party.addMember(uuid);
        playerParties.put(uuid, party);
        party.broadcastMessage("&a" + player.getName() + " has joined the party!");
    }

    /**
     * Removes a player from their current party.
     * <p>
     * If the player was the leader, leadership is automatically transferred to
     * another member. If the party becomes empty, it is naturally disposed of.
     *
     * @param player The player to remove.
     */
    public void leaveParty(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        Party party = getParty(player);
        if (party == null) return;

        party.removeMember(uuid);
        playerParties.remove(uuid);
        party.broadcastMessage("&c" + player.getName() + " has left the party.");

        if (party.getMembers().isEmpty()) return;

        if (party.isLeader(uuid)) {
            Optional<UUID> newLeader = party.getMembers().stream().findFirst();
            if (newLeader.isPresent()) {
                party.setLeader(newLeader.get());
                party.broadcastMessage("&e" + Bukkit.getOfflinePlayer(newLeader.get()).getName() + " is the new party leader.");
            }
        }
    }

    /**
     * Disbands a party entirely and clears all member associations.
     *
     * @param party The {@link Party} to disband.
     */
    public void disbandParty(@NotNull Party party) {
        party.broadcastMessage("&cThe party has been disbanded.");
        for (UUID member : party.getMembers()) playerParties.remove(member);
    }

    /**
     * Monitors player quitting to remove them from potential parties.
     *
     * @param event The entity damage event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        if (hasParty(event.getPlayer())) leaveParty(event.getPlayer());
    }

    /**
     * Monitors combat interactions to enforce party-based damage rules.
     * <p>
     * This handler checks if both the damager and the victim are in the same
     * party and cancels the event if "Friendly Fire" is disabled.
     *
     * @param event The entity damage event.
     */
    @EventHandler
    public void onEntityDamageEntity(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;
        Party party = getParty(damager);
        if (party == null || !party.isMember(victim) || party.isFriendlyFireEnabled()) return;
        event.setCancelled(true);
    }
}
