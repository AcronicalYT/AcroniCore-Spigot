package uk.acronical.sql;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private final String tableName;
    private final List<String> columns = new ArrayList<>();
    private String primaryKey;

    /**
     * Creates a new TableBuilder for the specified table name.
     *
     * @param tableName The name of the table to be created.
     */
    public TableBuilder(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Adds a column to the table with the specified name and data type.
     *
     * @param columnName The name of the column to be added.
     * @param dataType The data type of the column (e.g., "VARCHAR(255)", "INT", etc.).
     * @return The current TableBuilder instance for method chaining.
     */
    public TableBuilder addColumn(String columnName, String dataType) {
        columns.add(columnName + " " + dataType);
        return this;
    }

    /**
     * Adds a column to the table with the specified name, data type, and constraints.
     *
     * @param columnName The name of the column to be added.
     * @param dataType The data type of the column (e.g., "VARCHAR(255)", "INT", etc.).
     * @param constraints Any additional constraints for the column (e.g., "NOT NULL", "UNIQUE", etc.).
     * @return The current TableBuilder instance for method chaining.
     */
    public TableBuilder addColumn(String columnName, String dataType, String constraints) {
        columns.add(columnName + " " + dataType + " " + constraints);
        return this;
    }

    /**
     * Sets the primary key for the table.
     *
     * @param columnName The name of the column to be set as the primary key.
     * @return The current TableBuilder instance for method chaining.
     */
    public TableBuilder setPrimaryKey(String columnName) {
        this.primaryKey = columnName;
        return this;
    }

    /**
     * Builds the SQL statement for creating the table based on the specified columns and primary key.
     *
     * @return The SQL statement for creating the table.
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
     * Executes the SQL statement to create the table using the provided SqlExecutor.
     * This is a helper method that combines the building and execution of the SQL statement in one step.
     *
     * @param executor The SqlExecutor instance used to execute the SQL statement for creating the table.
     */
    public void create(SqlExecutor executor) {
        executor.update(this.build());
    }
}
