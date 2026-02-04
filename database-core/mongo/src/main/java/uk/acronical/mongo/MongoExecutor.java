package uk.acronical.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class MongoExecutor {

    private final MongoWrapper mongoWrapper;

    /**
     * Constructs a new MongoExecutor with a provided wrapper.
     *
     * @param mongoWrapper The wrapper instance used to access MongoDB collections.
     */
    public MongoExecutor(MongoWrapper mongoWrapper) {
        this.mongoWrapper = mongoWrapper;
    }

    /**
     * Asynchronously saves or updates a document using an upsert operation.
     *
     * @param collection The name of the collection.
     * @param keyField The field name used to identify the document.
     * @param keyValue The value of the identifying field.
     * @param data The document data to save.
     * @return A CompletableFuture that completes when the save is done.
     */
    public CompletableFuture<Void> save(String collection, String keyField, Object keyValue, Document data) {
        return CompletableFuture.runAsync(() -> {
            MongoCollection<Document> returnedCollection = mongoWrapper.getCollection(collection);

            returnedCollection.replaceOne(Filters.eq(keyField, keyValue), data, new ReplaceOptions().upsert(true));
        });
    }

    /**
     * Asynchronously finds a single document by a specific field and value.
     *
     * @param collection The name of the collection.
     * @param keyField The field name to filter by.
     * @param keyValue The value to match.
     * @return A CompletableFuture containing the found Document, or null.
     */
    public CompletableFuture<Document> find(String collection, String keyField, Object keyValue) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> returnedCollection = mongoWrapper.getCollection(collection);
            return returnedCollection.find(Filters.eq(keyField, keyValue)).first();
        });
    }
}
