package uk.acronical.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.CompletableFuture;

public class RedisCache {

    private final RedisDatabase database;

    /**
     * Initialise the RedisCache using the database.
     *
     * @param database The database to cache.
     */
    public RedisCache(RedisDatabase database) {
        this.database = database;
    }

    /**
     * Set a database value that will expire after a specified time.
     *
     * @param key The key associated to the value.
     * @param value The value to set.
     * @param secondsToLive The time until the value expires.
     */
    public CompletableFuture<Void> set(String key, String value, int secondsToLive) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                jedis.setex(key, secondsToLive, value);
            }
        });
    }

    /**
     * Gets the value of a given key stored in the database.
     *
     * @param key The key to search for in the database.
     * @return The value associated with the key OR null if the key doesn't exist.
     */
    public CompletableFuture<String> get(String key) {
        return CompletableFuture.supplyAsync(() -> {
           try (Jedis jedis = database.getResource()) {
               return jedis.get(key);
           }
        });
    }

    /**
     * Checks if a key exists in the database.
     *
     * @param key The key to check for in the database.
     * @return True if the key exists, false otherwise.
     */
    public CompletableFuture<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                return jedis.exists(key);
            }
        });
    }

}
