package uk.acronical.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoWrapper {

    private MongoClient client;
    private MongoDatabase database;

    /**
     * Connects to a MongoDB server and initialises the database instance.
     *
     * @param connectionUri The connection string (e.g., mongodb://localhost:27017).
     * @param databaseName The name of the database to access.
     */
    public void connect(String connectionUri, String databaseName) {
        this.client = MongoClients.create(connectionUri);
        this.database = client.getDatabase(databaseName);
    }

    /**
     * Retrieves a specific MongoDB collection by name.
     *
     * @param name The name of the collection to retrieve.
     * @return The requested MongoCollection.
     * @throws IllegalStateException If the database connection has not been established.
     */
    public MongoCollection<Document> getCollection(String name) {
        if (database == null) throw new IllegalStateException("Unable to connect to mongodb!");
        return database.getCollection(name);
    }

    /**
     * Closes the MongoDB client and releases all pooled resources.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
