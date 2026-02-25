package uk.acronical.social;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service for managing and querying {@link SocialProfile} data.
 * <p>
 * This service facilitates relationship checks such as friendship verification
 * and block lists, utilised to moderate player interactions across the server.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class SocialService implements Listener {

    public static final String friendRelationString = "FRIEND";
    public static final String blockedRelationString = "BLOCKED";
    public static final String outgoingRequestRelationString = "REQUEST_OUTGOING";
    public static final String incomingRequestRelationString = "REQUEST_INCOMING";

    private final Map<UUID, SocialProfile> activeProfiles = new ConcurrentHashMap<>();

    /**
     * Caches a social profile for an active player session.
     *
     * @param socialProfile The {@link SocialProfile} to cache.
     */
    public void cacheProfile(@NotNull SocialProfile socialProfile) {
        activeProfiles.put(socialProfile.getOwner(), socialProfile);
    }

    /**
     * Retrieves the social profile for a specific {@link UUID}.
     * <p>
     * If the profile is not currently cached, a new instance is returned.
     *
     * @param uuid The unique identifier of the player.
     * @return The associated {@link SocialProfile}.
     */
    @NotNull
    public SocialProfile getProfile(@NotNull UUID uuid) {
        return activeProfiles.getOrDefault(uuid, new SocialProfile(uuid));
    }

    /**
     * Retrieves the social profile for a specific player.
     *
     * @param player The player instance.
     * @return The associated {@link SocialProfile}.
     */
    @NotNull
    public SocialProfile getProfile(@NotNull Player player) {
        return getProfile(player.getUniqueId());
    }

    /**
     * Checks if the viewer has blocked the target player.
     *
     * @param viewer The player viewing.
     * @param target The player being checked.
     * @return {@code true} if the target is blocked by the viewer.
     */
    public boolean isBlocked(@NotNull Player viewer, @NotNull Player target) {
        return getProfile(viewer).hasRelation(target, blockedRelationString);
    }

    /**
     * Verifies if two players share a mutual friendship.
     *
     * @param playerA The first player.
     * @param playerB The second player.
     * @return {@code true} if both players have the friend relation set for each other.
     */
    public boolean areFriends(@NotNull Player playerA, @NotNull Player playerB) {
        SocialProfile profileA = getProfile(playerA);
        SocialProfile profileB = getProfile(playerB);
        return profileA.hasRelation(playerB.getUniqueId(), friendRelationString) && profileB.hasRelation(playerA.getUniqueId(), friendRelationString);
    }

    /**
     * Automatically removes profiles from the cache when players disconnect
     * to ensure memory is properly managed.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        activeProfiles.remove(event.getPlayer().getUniqueId());
    }
}
