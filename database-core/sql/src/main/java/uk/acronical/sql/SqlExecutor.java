package uk.acronical.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SqlExecutor {

    private final SqlDatabase sqlDatabase;

    /**
     * Constructor for the SqlExecutor class
     *
     * @param sqlDatabase The SqlDatabase instance.
     */
    public SqlExecutor(SqlDatabase sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    /**
     * Asynchronously executes a SQL update, insert, or delete statement.
     *
     * @param sql The SQL statement to execute.
     * @param parameters The values to bind to the query placeholders.
     * @return A CompletableFuture representing the pending database task.
     */
    public CompletableFuture<Void> update(String sql, Object... parameters) {
        return CompletableFuture.runAsync(() -> {
           try (Connection connection = sqlDatabase.getConnection(); PreparedStatement statement = prepare(connection, sql, parameters)) {
               statement.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           }
        });
    }

    /**
     * Asynchronously executes a SQL query and processes the result.
     *
     * @param sql The SQL query to execute.
     * @param handler The function to map the ResultSet to a return object.
     * @param parameters The values to bind to the query placeholders.
     * @param <T> The type of the object returned by the handler.
     * @return A CompletableFuture containing the processed result or null.
     */
    public <T> CompletableFuture<T> query(String sql, Function<ResultSet, T> handler, Object... parameters) {
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
     *
     * @param connection The database connection to use.
     * @param sql The SQL query string.
     * @param parameters The values to map to the statement placeholders.
     * @return A configured PreparedStatement.
     * @throws SQLException If a database access error occurs.
     */
    private PreparedStatement prepare(Connection connection, String sql, Object... parameters) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) stmt.setObject(i + 1, parameters[i]);
        return stmt;
    }
}
