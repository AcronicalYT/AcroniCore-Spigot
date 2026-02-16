package uk.acronical.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for the MongoDB Java driver to manage connections and lifecycle.
 * <p>
 * This class abstracts the {@link MongoClient} and {@link MongoDatabase} instances,
 * providing a centralized point for collection access. The underlying client
 * maintains an internal connection pool and is thread-safe.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class MongoWrapper {

    private MongoClient client;
    private MongoDatabase database;

    /**
     * Connects to a MongoDB server and initialises the database instance.
     *
     * @param connectionUri The connection string (e.g., {@code mongodb://localhost:27017}).
     * @param databaseName  The name of the database to access.
     */
    public void connect(@NotNull String connectionUri, @NotNull String databaseName) {
        this.client = MongoClients.create(connectionUri);
        this.database = client.getDatabase(databaseName);
    }

    /**
     * Retrieves a MongoDB collection by name.
     * <p>
     * This method can be safely called from multiple threads simultaneously
     * provided that {@link #connect(String, String)} has already been invoked.
     *
     * @param name The name of the collection to retrieve.
     * @return The requested {@link MongoCollection}.
     * @throws IllegalStateException If the database connection has not been established.
     */
    public MongoCollection<Document> getCollection(@NotNull String name) {
        if (database == null) throw new IllegalStateException("Unable to connect to mongodb!");
        return database.getCollection(name);
    }

    /**
     * Closes the MongoDB client and releases all pooled resources.
     * <p>
     * This should be called during the plugin's shutdown phase (e.g., {@code onDisable})
     * to ensure no socket leaks occur.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
