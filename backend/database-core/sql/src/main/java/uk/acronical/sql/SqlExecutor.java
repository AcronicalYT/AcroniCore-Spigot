package uk.acronical.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Provides asynchronous execution for SQL queries and updates.
 * <p>
 * This executor utilises {@link CompletableFuture} to wrap blocking JDBC operations,
 * ensuring database tasks do not stall the application's main thread.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class SqlExecutor {

    private final SqlDatabase sqlDatabase;

    /**
     * Initialises the {@link SqlExecutor} with a database provider.
     *
     * @param sqlDatabase The {@link SqlDatabase} instance used to fetch connections.
     */
    public SqlExecutor(@NotNull SqlDatabase sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    /**
     * Asynchronously executes a SQL update, insert, or delete statement.
     *
     * @param sql        The SQL statement to execute.
     * @param parameters The values to bind to the query placeholders (?).
     * @return A {@link CompletableFuture} that completes when the update is finished.
     */
    public CompletableFuture<Void> update(@NotNull String sql, Object... parameters) {
        return CompletableFuture.runAsync(() -> {
           try (Connection connection = sqlDatabase.getConnection(); PreparedStatement statement = prepare(connection, sql, parameters)) {
               statement.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    /**
     * Asynchronously executes a SQL query and processes the first result row.
     * <p>
     * The provided {@code handler} is applied only if the {@link ResultSet}
     * contains at least one row.
     *
     * @param <T>        The type of the object produced by the handler.
     * @param sql        The SQL query to execute.
     * @param handler    The function to map the {@link ResultSet} to a return object.
     * @param parameters The values to bind to the query placeholders (?).
     * @return A {@link CompletableFuture} containing the mapped result, or {@code null}
     * if no records were found or an error occurred.
     */
    public <T> CompletableFuture<T> query(@NotNull String sql, @NotNull Function<ResultSet, T> handler, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = sqlDatabase.getConnection(); PreparedStatement stmt = prepare(conn, sql, parameters); ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? handler.apply(rs) : null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Prepares a SQL statement and binds the provided parameters.
     * <p>
     * Note: The caller is responsible for ensuring the returned {@link PreparedStatement}
     * is closed to prevent resource leaks.
     *
     * @param connection The database connection to use.
     * @param sql        The SQL query string.
     * @param parameters The values to map to the statement placeholders.
     * @return A configured {@link PreparedStatement}.
     * @throws SQLException If a database access error occurs.
     */
    private PreparedStatement prepare(@NotNull Connection connection, @NotNull String sql, Object... parameters) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) stmt.setObject(i + 1, parameters[i]);
        return stmt;
    }
}
