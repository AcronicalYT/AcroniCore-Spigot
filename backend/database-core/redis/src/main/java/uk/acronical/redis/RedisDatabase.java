package uk.acronical.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisDatabase {

    private JedisPool pool;

    /**
     * Connect to a redis database that is not password protected.
     *
     * @param host The hostname of the database, "localhost" or the IP.
     * @param port The port the database is listening on.
     */
    public void connect(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        config.setMinEvictableIdleDuration(Duration.ofMinutes(1));
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        config.setTestOnBorrow(true);

        this.pool = new JedisPool(config, host, port, 2000);
    }

    /**
     * Connect to a redis database that is password protected.
     *
     * @param host The hostname of the database, "localhost" or the IP.
     * @param port The port the database is listening on.
     * @param password The password to access the database.
     */
    public void connect(String host, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        config.setMinEvictableIdleDuration(Duration.ofMinutes(1));
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        config.setTestOnBorrow(true);

        this.pool = new JedisPool(config, host, port, 2000, password);
    }

    /**
     * Gets a resource from the pool
     *
     * @return The requested resource.
     */
    public Jedis getResource() {
        if (pool == null || pool.isClosed()) throw new IllegalStateException("Unable to access redis database, the connection could be closed!");
        return pool.getResource();
    }

    /**
     * Closes the connection to the redis database.
     */
    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
