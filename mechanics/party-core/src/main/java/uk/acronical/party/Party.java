package uk.acronical.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.StringUtils;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a group of players associated for cooperative gameplay.
 * <p>
 * This model manages membership, leadership, and party-wide communication.
 * It is designed to be lightweight and thread-safe, utilised by a central
 * party service for lifecycle management.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class Party {

    private final UUID partyId;
    private UUID leader;

    private final Set<UUID> members = ConcurrentHashMap.newKeySet();
    private final Set<UUID> invited = ConcurrentHashMap.newKeySet();

    private boolean friendlyFire = false;

    /**
     * Initialises a new party with a specific leader.
     *
     * @param leader The {@link UUID} of the player creating the party.
     */
    public Party(@NotNull UUID leader) {
        this.partyId = UUID.randomUUID();
        this.leader = leader;
        this.members.add(leader);
    }

    /**
     * Initialises a new party with a specific leader.
     *
     * @param leader The {@link Player} instance creating the party.
     */
    public Party(@NotNull Player leader) {
        this(leader.getUniqueId());
    }

    @NotNull
    public UUID getPartyId() {
        return partyId;
    }

    @NotNull
    public UUID getLeader() {
        return leader;
    }

    public boolean isFriendlyFireEnabled() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Retrieves an unmodifiable view of the current party members.
     *
     * @return A set of member {@link UUID}s.
     */
    @NotNull
    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isMember(@NotNull UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isMember(@NotNull Player player) {
        return isMember(player.getUniqueId());
    }

    public boolean isLeader(@NotNull UUID uuid) {
        return leader.equals(uuid);
    }

    public boolean isLeader(@NotNull Player player) {
        return isLeader(player.getUniqueId());
    }


    /**
     * Adds a player to the party and clears any existing invitation for them.
     *
     * @param uuid The {@link UUID} of the player joining.
     */
    public void addMember(@NotNull UUID uuid) {
        members.add(uuid);
        invited.remove(uuid);
    }

    public void addMember(@NotNull Player player) {
        addMember(player.getUniqueId());
    }

    public void removeMember(@NotNull UUID uuid) {
        members.remove(uuid);
    }

    public void removeMember(@NotNull Player player) {
        removeMember(player.getUniqueId());
    }

    /**
     * Transfers leadership to another member of the party.
     *
     * @param newLeader The {@link UUID} of the member to become leader.
     */
    public void setLeader(@NotNull UUID newLeader) {
        if (members.contains(newLeader)) this.leader = newLeader;
    }

    public void setLeader(@NotNull Player player) {
        setLeader(player.getUniqueId());
    }

    public void invite(@NotNull UUID uuid) {
        invited.add(uuid);
    }

    public void invite(@NotNull Player player) {
        invite(player.getUniqueId());
    }

    public boolean isInvited(@NotNull UUID uuid) {
        return invited.contains(uuid);
    }

    public boolean isInvited(@NotNull Player player) {
        return isInvited(player.getUniqueId());
    }

    /**
     * Dispatches a coloured message to all online party members.
     *
     * @param message The message text to broadcast.
     */
    public void broadcastMessage(@NotNull String message) {
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) player.sendMessage(StringUtils.colour(message));
        }
    }
}
