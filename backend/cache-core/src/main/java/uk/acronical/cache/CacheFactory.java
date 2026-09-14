package uk.acronical.cache;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * A utility factory for generating standardised cache instances across the framework.
 * <p>
 * This class provides pre-configured builders for the {@link ExpiringCache},
 * ensuring consistent memory constraints and time-to-live (TTL) policies.
 *
 * @author Acronical
 * @since 1.0.6
 */
public final class CacheFactory {

    private static final long DEFAULT_MAX_SIZE = 10_000L;

    private CacheFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Creates a new time-based cache with a default maximum capacity.
     *
     * @param duration The time before an entry expires after being written.
     * @param unit     The unit of time for the duration.
     * @param <K>      The type of keys.
     * @param <V>      The type of mapped values.
     * @return A newly initialised expiring cache.
     */
    @NotNull
    public static <K, V> ExpiringCache<K, V> createTimeCache(long duration, @NotNull TimeUnit unit) {
        return new ExpiringCache<>(duration, unit, DEFAULT_MAX_SIZE);
    }

    /**
     * Creates a new time-based cache with a strictly defined maximum capacity.
     *
     * @param duration The time before an entry expires after being written.
     * @param unit     The unit of time for the duration.
     * @param maxSize  The maximum number of entries before eviction occurs.
     * @param <K>      The type of keys.
     * @param <V>      The type of mapped values.
     * @return A newly initialised expiring cache.
     */
    @NotNull
    public static <K, V> ExpiringCache<K, V> createBoundsCache(long duration, @NotNull TimeUnit unit, long maxSize) {
        return new ExpiringCache<>(duration, unit, maxSize);
    }
}