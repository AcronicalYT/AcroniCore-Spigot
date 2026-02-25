package uk.acronical.social;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents the social data and relationships associated with a specific player.
 * <p>
 * This model facilitates the tracking of custom relationship types (e.g., Friend, Blocked,
 * Ally) and is designed to be utilised within a session-based persistence system.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class SocialProfile {

    private final UUID owner;
    private final Map<UUID, String> relationships = new ConcurrentHashMap<>();

    /**
     * Initialises a new social profile via {@link UUID}.
     *
     * @param owner The unique identifier of the profile owner.
     */
    public SocialProfile(@NotNull UUID owner) {
        this.owner = owner;
    }

    /**
     * Initialises a new social profile via {@link Player}.
     *
     * @param owner The player instance owning this profile.
     */
    public SocialProfile(@NotNull Player owner) {
        this(owner.getUniqueId());
    }

    /**
     * Retrieves the {@link UUID} of the profile owner.
     *
     * @return The owner's unique identifier.
     */
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    /**
     * Establishes or updates a relationship with a target player.
     *
     * @param target       The {@link UUID} of the target player.
     * @param relationship The type of relationship (e.g., "FRIEND").
     */
    public void setRelation(@NotNull UUID target, @NotNull String relationship) {
        if (owner.equals(target)) return;
        relationships.put(target, relationship);
    }

    /**
     * Establishes or updates a relationship with a target player.
     *
     * @param target       The target {@link Player}.
     * @param relationship The type of relationship.
     */
    public void setRelation(@NotNull Player target, @NotNull String relationship) {
        setRelation(target.getUniqueId(), relationship);
    }

    /**
     * Sever any existing relationship with the target player.
     *
     * @param target The {@link UUID} to remove.
     */
    public void removeRelation(@NotNull UUID target) {
        relationships.remove(target);
    }

    /**
     * Sever any existing relationship with the target player.
     *
     * @param target The {@link Player} to remove.
     */
    public void removeRelation(@NotNull Player target) {
        removeRelation(target.getUniqueId());
    }

    /**
     * Retrieves the current relationship type for a specific player.
     *
     * @param target The {@link UUID} to check.
     * @return The relationship string, or {@code null} if none exists.
     */
    @Nullable
    public String getRelation(@NotNull UUID target) {
        return relationships.get(target);
    }

    /**
     * Retrieves the current relationship type for a specific player.
     *
     * @param target The {@link Player} to check.
     * @return The relationship string, or {@code null} if none exists.
     */
    public String getRelation(Player target) {
        return getRelation(target.getUniqueId());
    }

    /**
     * Checks if a specific relationship type exists with the target player.
     *
     * @param target       The {@link UUID} of the target.
     * @param relationship The relationship type to verify (case-insensitive).
     * @return {@code true} if the relationship matches.
     */
    public boolean hasRelation(@NotNull UUID target, @NotNull String relationship) {
        String type = relationships.get(target);
        return type != null && type.equalsIgnoreCase(relationship);
    }

    /**
     * Checks if a specific relationship type exists with the target player.
     *
     * @param target       The {@link Player} of the target.
     * @param relationship The relationship type to verify (case-insensitive).
     * @return {@code true} if the relationship matches.
     */
    public boolean hasRelation(Player target, String relationship) {
        return hasRelation(target.getUniqueId(), relationship);
    }

    /**
     * Retrieves all players associated with a specific relationship type.
     *
     * @param relationship The relationship type to filter by.
     * @return A set of unique identifiers for matching players.
     */
    @NotNull
    public Set<UUID> getAllOfType(@NotNull String relationship) {
        return relationships.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(relationship)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Provides an unmodifiable view of the raw relationship data.
     * <p>
     * Primarily utilised for serialisation purposes.
     *
     * @return A map of UUIDs to relationship strings.
     */
    @NotNull
    public Map<UUID, String> getRawData() {
        return Collections.unmodifiableMap(relationships);
    }

    /**
     * Populates the profile with raw data, typically following a database load.
     *
     * @param data The map of relationships to load.
     */
    public void loadRawData(@NotNull Map<UUID, String> data) {
        relationships.clear();
        relationships.putAll(data);
    }
}
