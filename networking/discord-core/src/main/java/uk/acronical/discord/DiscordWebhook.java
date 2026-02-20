package uk.acronical.discord;

import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.discord.models.DiscordPayload;
import uk.acronical.http.Http;

import java.util.concurrent.CompletableFuture;

/**
 * A service for dispatching messages to Discord via Webhooks.
 * <p>
 * This utility utilises the {@link Http} package to asynchronously transmit
 * {@link DiscordPayload} objects to a specified Discord endpoint.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class DiscordWebhook {

    private final String webhookUrl;

    /**
     * Initialises a new {@link DiscordWebhook} instance.
     *
     * @param webhookUrl The unique URL provided by Discord for the target channel.
     */
    public DiscordWebhook(@NotNull String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    /**
     * Dispatches a complex {@link DiscordPayload} to the webhook asynchronously.
     *
     * @param payload The structured message payload including content or embeds.
     * @return A {@link CompletableFuture} that completes when the request is finalised.
     */
    @NotNull
    public CompletableFuture<Void> send(@NotNull DiscordPayload payload) {
        return Http.post(webhookUrl).body(payload).post().thenAccept((response) -> {
           if (!response.isSuccessful()) {
               LoggerUtils.warn("Failed to send message to Discord webhook: " + response.getStatusCode());
               LoggerUtils.debug("Response body: " + response.getBody());
           }
        });
    }

    /**
     * Dispatches a simple plain-text message to the webhook.
     *
     * @param content The text content to send.
     */
    public void sendText(@NotNull String content) {
        send(new DiscordPayload().setContent(content));
    }
}
