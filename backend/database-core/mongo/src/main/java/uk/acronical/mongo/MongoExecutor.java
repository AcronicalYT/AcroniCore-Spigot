package uk.acronical.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Provides asynchronous execution for MongoDB operations.
 * <p>
 * This executor wraps blocking MongoDB driver calls in {@link CompletableFuture}s,
 * allowing for non-blocking database interactions on the server's main thread.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class MongoExecutor {

    private final MongoWrapper mongoWrapper;

    /**
     * Constructs a new {@link MongoExecutor}.
     *
     * @param mongoWrapper The wrapper used to access MongoDB collections.
     */
    public MongoExecutor(@NotNull MongoWrapper mongoWrapper) {
        this.mongoWrapper = mongoWrapper;
    }

    /**
     * Asynchronously saves or updates a document.
     * <p>
     * This operation uses an "upsert" strategy: if a document matches the
     * {@code keyField} and {@code keyValue}, it is replaced; otherwise, a
     * new document is inserted.
     *
     * @param collection The name of the collection to target.
     * @param keyField   The field name used to identify the document (e.g., "uuid").
     * @param keyValue   The value of the identifying field.
     * @param data       The document data to persist.
     * @return A {@link CompletableFuture} that completes once the operation is finished.
     */
    public CompletableFuture<Void> save(@NotNull String collection, @NotNull String keyField, @NotNull Object keyValue, @NotNull Document data) {
        return CompletableFuture.runAsync(() -> {
            MongoCollection<Document> returnedCollection = mongoWrapper.getCollection(collection);

            returnedCollection.replaceOne(Filters.eq(keyField, keyValue), data, new ReplaceOptions().upsert(true));
        });
    }

    /**
     * Asynchronously retrieves a single document from the database.
     *
     * @param collection The name of the collection to search.
     * @param keyField   The field name to filter by.
     * @param keyValue   The value to match.
     * @return A {@link CompletableFuture} containing the found {@link Document},
     * or {@code null} if no match is found.
     */
    public CompletableFuture<Document> find(@NotNull String collection, @NotNull String keyField, @NotNull Object keyValue) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> returnedCollection = mongoWrapper.getCollection(collection);
            return returnedCollection.find(Filters.eq(keyField, keyValue)).first();
        });
    }
}
