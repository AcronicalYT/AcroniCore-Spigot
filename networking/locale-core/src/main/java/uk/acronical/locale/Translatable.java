package uk.acronical.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A fluent builder for creating and dispatching localised messages with placeholders.
 * <p>
 * This class handles the retrieval of translations from {@link LocaleManager} and
 * performs variable replacement before sending the final message to a recipient.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class Translatable {

    private final LocaleManager manager;
    private final String key;
    private final Map<String, String> placeholders = new HashMap<>();

    /**
     * Initialises a new translatable message.
     *
     * @param manager The {@link LocaleManager} to retrieve translations from.
     * @param key     The unique key for the message.
     */
    public Translatable(@NotNull LocaleManager manager, @NotNull String key) {
        this.manager = manager;
        this.key = key;
    }

    /**
     * Injects a placeholder and its replacement value into the message.
     *
     * @param placeholder The variable string to be replaced (e.g., "{player}").
     * @param value       The value to be inserted.
     * @return The current {@link Translatable} instance.
     */
    public Translatable withPlaceholder(@NotNull String placeholder, @NotNull String value) {
        placeholders.put(placeholder, value);
        return this;
    }

    /**
     * Dispatches the localised and formatted message to a player.
     *
     * @param player The player recipient whose locale will be utilised.
     */
    public void send(@NotNull Player player) {
        String locale = player.getLocale();
        player.sendMessage(format(locale));
    }

    /**
     * Dispatches the localised and formatted message to a {@link CommandSender}.
     * <p>
     * If the sender is a {@link Player}, their locale is used; otherwise, the
     * default locale is utilised.
     *
     * @param sender The sender recipient.
     */
    public void send(@NotNull CommandSender sender) {
        String locale = sender instanceof Player ? ((Player) sender).getLocale() : null;
        sender.sendMessage(format(locale));
    }

    /**
     * Finalises the message by retrieving the translation and replacing all placeholders.
     *
     * @param locale The locale to look up.
     * @return The fully formatted message string.
     */
    @NotNull
    private String format(@Nullable String locale) {
        String message = manager.getMessage(locale, key);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return message;
    }
}
