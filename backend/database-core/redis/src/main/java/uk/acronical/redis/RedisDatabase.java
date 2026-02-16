package uk.acronical.redis;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * A wrapper for the Jedis library to manage a connection pool to a Redis database.
 * <p>
 * This class handles the lifecycle of a {@link JedisPool}, providing thread-safe
 * access to Redis resources.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class RedisDatabase {

    private JedisPool pool;

    /**
     * Connects to a Redis database that is not password protected.
     *
     * @param host The hostname or IP of the database.
     * @param port The port the database is listening on.
     */
    public void connect(@NotNull String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        config.setMinEvictableIdleDuration(Duration.ofMinutes(1));
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        config.setTestOnBorrow(true);

        this.pool = new JedisPool(config, host, port, 2000);
    }

    /**
     * Connects to a password-protected Redis database.
     *
     * @param host     The hostname or IP of the database.
     * @param port     The port the database is listening on.
     * @param password The password required for authentication.
     */
    public void connect(@NotNull String host, int port, @NotNull String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        config.setMinEvictableIdleDuration(Duration.ofMinutes(1));
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        config.setTestOnBorrow(true);

        this.pool = new JedisPool(config, host, port, 2000, password);
    }

    /**
     * Retrieves a {@link Jedis} resource from the connection pool.
     * <p>
     * Resources retrieved from this method should be used within a
     * try-with-resources block to ensure they are returned to the pool.
     *
     * @return The requested {@link Jedis} resource.
     * @throws IllegalStateException If the pool has not been initialised or is closed.
     */
    public Jedis getResource() {
        if (pool == null || pool.isClosed()) throw new IllegalStateException("Unable to access redis database, the connection could be closed!");
        return pool.getResource();
    }

    /**
     * Closes the connection pool and releases all associated resources.
     * <p>
     * This should be called during the application shutdown phase to prevent
     * resource leaks.
     */
    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
