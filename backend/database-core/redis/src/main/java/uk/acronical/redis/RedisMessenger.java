package uk.acronical.redis;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import uk.acronical.common.LoggerUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * A utility class for sending and receiving messages through Redis channels.
 * <p>
 * This class provides methods to publish messages to a channel and subscribe
 * to channels to receive messages asynchronously. It manages the subscription
 * lifecycle and ensures that resources are properly released when no longer needed.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class RedisMessenger {

    private final RedisDatabase database;
    private final ExecutorService subscriptionThread;
    private JedisPubSub activeSubscription;

    /**
     * Initialises the messenger with the database instance to manage.
     *
     * @param database The Redis database wrapper used for messaging.
     */
    public RedisMessenger(@NotNull RedisDatabase database) {
        this.database = database;
        this.subscriptionThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Asynchronously sends a message to the specified channel.
     *
     * @param channel The channel to target (e.g., {@code "global-chat"}).
     * @param message The message string to broadcast.
     */
    public void publish(@NotNull String channel, @NotNull String message) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                jedis.publish(channel, message);
            }
        });
    }

    /**
     * Subscribes to a channel to listen for incoming messages.
     * <p>
     * As Redis subscriptions are blocking operations, this method executes the
     * listener on a dedicated background thread to prevent halting the main application.
     *
     * @param channel The channel to monitor.
     * @param handler A consumer to process received messages, providing the
     * channel name and the message content.
     */
    public void subscribe(@NotNull String channel, @NotNull BiConsumer<String, String> handler) {
        this.activeSubscription = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                handler.accept(channel, message);
            }
        };

        subscriptionThread.submit(() -> {
           try (Jedis jedis = database.getResource()) {
               jedis.subscribe(activeSubscription, channel);
           } catch (Exception e) {
               LoggerUtils.severe("An error occured during a redis subscription");
               LoggerUtils.severe(e.getMessage());
           }
        });
    }

    /**
     * Unsubscribes from the active channel and shuts down the internal thread pool.
     * <p>
     * This should be called during the plugin shutdown phase to ensure the
     * background thread is terminated correctly.
     */
    public void close() {
        if (activeSubscription != null) activeSubscription.unsubscribe();
        subscriptionThread.shutdownNow();
    }
}
