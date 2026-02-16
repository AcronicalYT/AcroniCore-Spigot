package uk.acronical.redis;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CompletableFuture;

/**
 * Provides asynchronous caching operations for a Redis database.
 * <p>
 * This class leverages a connection pool via {@link RedisDatabase} to perform
 * non-blocking cache lookups and storage.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class RedisCache {

    private final RedisDatabase database;

    /**
     * Constructs a new {@link RedisCache} instance.
     *
     * @param database The Redis database wrapper providing connections.
     */
    public RedisCache(@NotNull RedisDatabase database) {
        this.database = database;
    }

    /**
     * Asynchronously sets a value with an expiration time.
     *
     * @param key           The key associated with the value.
     * @param value         The value to store.
     * @param secondsToLive The time, in seconds, until the key expires.
     * @return A {@link CompletableFuture} that completes when the value is set.
     */
    public CompletableFuture<Void> set(@NotNull String key, @NotNull String value, int secondsToLive) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                jedis.setex(key, secondsToLive, value);
            }
        });
    }

    /**
     * Asynchronously retrieves a value by its key.
     *
     * @param key The key to look up.
     * @return A {@link CompletableFuture} containing the value,
     * or {@code null} if the key does not exist or has expired.
     */
    public CompletableFuture<String> get(@NotNull String key) {
        return CompletableFuture.supplyAsync(() -> {
           try (Jedis jedis = database.getResource()) {
               return jedis.get(key);
           }
        });
    }

    /**
     * Asynchronously checks if a key exists in the cache.
     *
     * @param key The key to check.
     * @return A {@link CompletableFuture} that is {@code true} if the key exists,
     * otherwise {@code false}.
     */
    public CompletableFuture<Boolean> exists(@NotNull String key) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                return jedis.exists(key);
            }
        });
    }
}
