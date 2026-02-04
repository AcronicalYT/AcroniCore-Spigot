package uk.acronical.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlDatabase {

    private HikariDataSource dataSource;

    /**
     * Connects to a remote database using {@code Hikari} and sets the {@code dataSource} variable.
     *
     * @param host The remote database hostname or ip address.
     * @param port The remote database port number.
     * @param database The remote database name.
     * @param user The remote database access user.
     * @param password The remote database access password/token.
     */
    public void connect(String host, int port, String database, String user, String password) {
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
     * Connects to a local db file, like the one found in SQLite.
     *
     * @param folder The folder object in which the .db file is stored.
     * @param fileName The name of the .db file.
     */
    public void connect(File folder, String fileName) {
        if (!folder.exists()) folder.mkdirs();

        HikariConfig config = new HikariConfig();

        config.setPoolName("AcroniCore-SQL-Pool");
        config.setJdbcUrl("jdbc:sqlite:" + new File(folder, fileName + ".db").getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Gets the current connection for the database.
     *
     * @return Returns the current connection.
     * @throws SQLException Exception when database is not connected or accessible.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("Unable to connect to the database!");
        return dataSource.getConnection();
    }

    /**
     * Closes the current connection.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
