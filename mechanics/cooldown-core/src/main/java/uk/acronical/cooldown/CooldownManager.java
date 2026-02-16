package uk.acronical.cooldown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple named cooldowns for players using a thread-safe registry.
 * <p>
 * This manager utilises nested {@link ConcurrentHashMap}s to track cooldowns by
 * {@link UUID} and a unique string key, allowing for granular time-based
 * restrictions across the plugin.
 *
 * @author Acronical
 * @since 1.0.1-SNAPSHOT
 */
public class CooldownManager {

    private final Map<UUID, Map<String, Cooldown>> registry = new ConcurrentHashMap<>();

    /**
     * Assigns a new {@link Cooldown} to a player for a specific key.
     *
     * @param uuid     The {@link UUID} of the player.
     * @param key      The identifier for the cooldown (e.g., {@code "enderpearl"}).
     * @param duration The length of time the cooldown should last.
     */
    public void set(@NotNull UUID uuid, @NotNull String key, @NotNull Duration duration) {
        registry.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(key, new Cooldown(duration));
    }

    /**
     * Checks if a specific cooldown is still active for a player.
     * <p>
     * This method performs "lazy cleanup": if a cooldown is found to be expired,
     * it is automatically removed from the registry to conserve memory.
     *
     * @param uuid The {@link UUID} of the player.
     * @param key  The identifier for the cooldown.
     * @return {@code true} if the cooldown is active; {@code false} if it has
     * expired or never existed.
     */
    public boolean isActive(@NotNull UUID uuid, @NotNull String key) {
        Map<String, Cooldown> playerCooldowns = registry.get(uuid);
        if (playerCooldowns == null) return false;

        Cooldown cooldown = playerCooldowns.get(key);
        if (cooldown == null) return false;

        if (!cooldown.isActive()) {
            playerCooldowns.remove(key, cooldown);
            if (playerCooldowns.isEmpty()) {
                registry.remove(uuid, playerCooldowns);
            }
            return false;
        }

        return true;
    }

    /**
     * Retrieves the {@link Cooldown} object for a player if it is active.
     *
     * @param uuid The {@link UUID} of the player.
     * @param key  The identifier for the cooldown.
     * @return The active {@link Cooldown} instance, or {@code null} if expired
     * or non-existent.
     */
    @Nullable
    public Cooldown get(@NotNull UUID uuid, @NotNull String key) {
        if (!isActive(uuid, key)) return null;
        return registry.get(uuid).get(key);
    }

    /**
     * Removes all active cooldowns for a specific player.
     *
     * @param uuid The {@link UUID} of the player to clear.
     */
    public void clear(@NotNull UUID uuid) {
        registry.remove(uuid);
    }
}
