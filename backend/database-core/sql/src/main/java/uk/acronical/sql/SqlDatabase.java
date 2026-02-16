package uk.acronical.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages SQL database connectivity using the HikariCP connection pool.
 * <p>
 * This class abstracts the setup for both remote MySQL/MariaDB databases and
 * local SQLite files, providing a unified way to retrieve thread-safe connections.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class SqlDatabase {

    private HikariDataSource dataSource;

    /**
     * Initialises a connection pool to a remote MySQL or MariaDB database.
     *
     * @param host     The hostname or IP address of the database server.
     * @param port     The port number the server is listening on.
     * @param database The name of the specific database to access.
     * @param user     The username used for authentication.
     * @param password The password or token used for authentication.
     */
    public void connect(@NotNull String host, int port, @NotNull String database, @NotNull String user, @NotNull String password) {
        HikariConfig config = new HikariConfig();

        config.setPoolName("AcroniCore-SQL-Pool");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Initialises a connection pool for a local SQLite database file.
     *
     * @param folder   The directory where the database file should be stored.
     * @param fileName The name of the database file (excluding the {@code .db} extension).
     */
    public void connect(@NotNull File folder, @NotNull String fileName) {
        if (!folder.exists()) folder.mkdirs();

        HikariConfig config = new HikariConfig();

        config.setPoolName("AcroniCore-SQL-Pool");
        config.setJdbcUrl("jdbc:sqlite:" + new File(folder, fileName + ".db").getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves a {@link Connection} from the pool.
     * <p>
     * Connections retrieved from this method should be closed immediately after
     * use (ideally within a try-with-resources block) to return them to the pool.
     *
     * @return A valid {@link Connection} instance.
     * @throws SQLException If the pool is uninitialised, closed, or a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("Unable to connect to the database!");
        return dataSource.getConnection();
    }

    /**
     * Shuts down the connection pool and releases all active connections.
     * <p>
     * This should be invoked during the application shutdown phase to ensure
     * all database resources are gracefully released.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
