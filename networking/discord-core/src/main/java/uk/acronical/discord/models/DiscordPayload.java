package uk.acronical.discord.models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root payload sent to a Discord Webhook.
 * <p>
 * This class serves as a container for the message content, webhook overrides
 * (username and avatar), and any associated {@link DiscordEmbed} instances.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class DiscordPayload {

    private String content;
    private String username;
    private String avatar_url;
    private final List<DiscordEmbed> embeds = new ArrayList<>();

    /**
     * Sets the plain-text message content.
     *
     * @param content The message body.
     * @return The current {@link DiscordPayload} instance.
     */
    public DiscordPayload setContent(@NotNull String content) {
        this.content = content;
        return this;
    }

    /**
     * Overrides the default username of the webhook.
     *
     * @param username The name to display for the message.
     * @return The current {@link DiscordPayload} instance.
     */
    public DiscordPayload setUsername(@NotNull String username) {
        this.username = username;
        return this;
    }

    /**
     * Overrides the default avatar of the webhook.
     *
     * @param avatar_url The URL of the image to use as the avatar.
     * @return The current {@link DiscordPayload} instance.
     */
    public DiscordPayload setAvatarUrl(@NotNull String avatar_url) {
        this.avatar_url = avatar_url;
        return this;
    }

    /**
     * Appends a {@link DiscordEmbed} to the payload.
     * <p>
     * Note: Discord limits webhooks to a maximum of 10 embeds per message.
     *
     * @param embed The embed to include.
     * @return The current {@link DiscordPayload} instance.
     */
    public DiscordPayload addEmbed(@NotNull DiscordEmbed embed) {
        this.embeds.add(embed);
        return this;
    }
}