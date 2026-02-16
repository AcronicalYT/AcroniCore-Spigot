package uk.acronical.sql;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for building SQL statements to create tables using a fluent API.
 * <p>
 * This builder allows for the programmatic definition of table structures,
 * supporting column definitions, constraints, and primary keys through method chaining.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class TableBuilder {

    private final String tableName;
    private final List<String> columns = new ArrayList<>();
    private String primaryKey;

    /**
     * Initialises a new {@link TableBuilder} for the specified table.
     *
     * @param tableName The name of the table to be created.
     */
    public TableBuilder(@NotNull String tableName) {
        this.tableName = tableName;
    }

    /**
     * Adds a column to the table definition.
     *
     * @param columnName The name of the column.
     * @param dataType   The SQL data type (e.g., {@code "VARCHAR(255)"} or {@code "INT"}).
     * @return The current {@link TableBuilder} instance for method chaining.
     */
    public TableBuilder addColumn(@NotNull String columnName, @NotNull String dataType) {
        columns.add(columnName + " " + dataType);
        return this;
    }

    /**
     * Adds a column to the table definition with additional constraints.
     *
     * @param columnName  The name of the column.
     * @param dataType    The SQL data type (e.g., {@code "TEXT"}).
     * @param constraints SQL constraints (e.g., {@code "NOT NULL"} or {@code "UNIQUE"}).
     * @return The current {@link TableBuilder} instance for method chaining.
     */
    public TableBuilder addColumn(@NotNull String columnName, @NotNull String dataType, @NotNull String constraints) {
        columns.add(columnName + " " + dataType + " " + constraints);
        return this;
    }

    /**
     * Sets the primary key for the table.
     *
     * @param columnName The name of the column to be designated as the primary key.
     * @return The current {@link TableBuilder} instance for method chaining.
     */
    public TableBuilder setPrimaryKey(@NotNull String columnName) {
        this.primaryKey = columnName;
        return this;
    }

    /**
     * Constructs the {@code CREATE TABLE IF NOT EXISTS} SQL statement.
     *
     * @return A formatted SQL string based on the defined columns and primary key.
     */
    public String build() {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        sql.append(tableName).append(" (");

        sql.append(String.join(", ", columns));

        if (primaryKey != null) {
            sql.append(", PRIMARY KEY (").append(primaryKey).append(")");
        }

        sql.append(");");

        return sql.toString();
    }

    /**
     * Builds and executes the creation statement asynchronously.
     * <p>
     * This is a convenience method that passes the generated SQL from {@link #build()}
     * to the provided {@link SqlExecutor}.
     *
     * @param executor The executor used to run the creation query.
     */
    public void create(SqlExecutor executor) {
        executor.update(this.build());
    }
}
