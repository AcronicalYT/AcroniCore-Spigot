package uk.acronical.cache;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A thread-safe, time-based cache for storing temporary data.
 * <p>
 * Utilises Google Guava to provide automatic expiration and size-based
 * eviction, protecting the framework from memory leaks whilst caching
 * expensive operations (e.g., database queries or UUID lookups).
 *
 * @param <K> The type of keys maintained by this cache.
 * @param <V> The type of mapped values.
 * @author Acronical
 * @since 1.0.6
 */
public class ExpiringCache<K, V> {

    private final Cache<K, V> cache;

    /**
     * Initialises a new expiring cache.
     *
     * @param duration The time before an entry expires after being written.
     * @param unit     The unit of time for the duration.
     * @param maxSize  The maximum number of entries before eviction occurs.
     */
    public ExpiringCache(long duration, @NotNull TimeUnit unit, long maxSize) {
        if (duration <= 0) throw new IllegalArgumentException("Cache duration must be greater than 0.");
        if (maxSize <= 0) throw new IllegalArgumentException("Cache maximum size must be greater than 0.");

        this.cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit).maximumSize(maxSize).build();
    }

    /**
     * Inserts a value into the cache.
     */
    public void put(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
    }

    /**
     * Retrieves an optional containing the value if it exists and is not expired.
     */
    @NotNull
    public Optional<V> get(@NotNull K key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    /**
     * Retrieves a value from the cache. If it does not exist, it safely computes
     * the value using the provided function and stores it.
     *
     * @param key      The key to look up.
     * @param fallback The function to execute if the key is missing or expired.
     * @return The cached or newly computed value.
     */
    @NotNull
    public V getOrCompute(@NotNull K key, @NotNull Function<K, V> fallback) {
        try {
            return cache.get(key, () -> {
                V value = fallback.apply(key);
                if (value == null) throw new IllegalStateException("Cache fallback function returned null for key: " + key);
                return value;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to compute value for key: " + key, e.getCause());
        }
    }

    /**
     * Manually invalidates a specific cache entry.
     */
    public void remove(@NotNull K key) {
        cache.invalidate(key);
    }

    /**
     * Checks if the cache currently holds a valid entry for the given key.
     */
    public boolean contains(@NotNull K key) {
        return cache.getIfPresent(key) != null;
    }

    /**
     * Wipes all data from the cache immediately.
     */
    public void clear() {
        cache.invalidateAll();
    }
}
