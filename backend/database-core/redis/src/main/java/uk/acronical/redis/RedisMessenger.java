package uk.acronical.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import uk.acronical.common.LoggerUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class RedisMessenger {

    private final RedisDatabase database;
    private final ExecutorService subscriptionThread;
    private JedisPubSub activeSubscription;

    /**
     * Initialise the messenger with the db to monitor and manage.
     *
     * @param database The database to send/recieve messages to/from.
     */
    public RedisMessenger(RedisDatabase database) {
        this.database = database;
        this.subscriptionThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends a message to the specified channel.
     *
     * @param channel The channel to send messages to. E.g. "global-chat".
     * @param message The message to send to the channel.
     */
    public void publish(String channel, String message) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = database.getResource()) {
                jedis.publish(channel, message);
            }
        });
    }

    /**
     * Listens for messages on the specified channel.
     *
     * @param channel The channel to listen to.
     * @param handler The code to run when message arrives (Channel, Message).
     */
    public void subscribe(String channel, BiConsumer<String, String> handler) {
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
     * Stop listening for messages.
     */
    public void close() {
        if (activeSubscription != null) activeSubscription.unsubscribe();
        subscriptionThread.shutdownNow();
    }
}
