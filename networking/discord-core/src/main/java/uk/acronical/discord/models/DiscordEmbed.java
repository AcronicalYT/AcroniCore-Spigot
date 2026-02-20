package uk.acronical.discord.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder-style model representing a Discord rich embed.
 * <p>
 * This class facilitates the construction of complex Discord messages, supporting
 * titles, descriptions, custom RGB colours, and field-based layouts.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class DiscordEmbed {

    private String title, description;
    private int colour;
    private final List<Field> fields = new ArrayList<>();
    private Footer footer;

    /**
     * Sets the title of the embed.
     *
     * @param title The title text.
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed setTitle(@NotNull String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the primary body text of the embed.
     *
     * @param description The body content.
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed setDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the side strip colour of the embed using RGB values.
     *
     * @param r Red component (0-255).
     * @param g Green component (0-255).
     * @param b Blue component (0-255).
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed setColour(int r, int g, int b) {
        this.colour = (r << 16) | (g << 8) | b;
        return this;
    }

    /**
     * Sets the side strip colour of the embed using a single hex integer.
     *
     * @param hex The hexadecimal colour value (e.g., 0xFF0000).
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed setColour(int hex) {
        this.colour = hex;
        return this;
    }

    /**
     * Appends a new field to the embed.
     *
     * @param name   The title of the field.
     * @param value  The content of the field.
     * @param inline Whether the field should be displayed alongside others.
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed addField(@NotNull String name, @NotNull String value, boolean inline) {
        this.fields.add(new Field(name, value, inline));
        return this;
    }

    /**
     * Initialises the footer section of the embed.
     *
     * @param text    The footer text.
     * @param iconUrl The URL of the small icon to display in the footer.
     * @return The current {@link DiscordEmbed} instance.
     */
    public DiscordEmbed setFooter(@NotNull String text, @Nullable String iconUrl) {
        this.footer = new Footer(text, iconUrl);
        return this;
    }

    private record Field(String name, String value, boolean inline) {}
    private record Footer(String text, String icon_url) {}
}
