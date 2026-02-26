package uk.acronical.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;
import uk.acronical.exception.impl.ConfigurationException;

/**
 * Handles retrieval of messages from a YAML configuration file, providing default values and logging warnings for missing messages.
 * <p>
 * This class extends {@link DefaultConfig} to provide automatic colour code
 * translation and logging warnings when requested keys are missing from {@code messages.yml}.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class MessagesConfig extends DefaultConfig {

    /**
     * Constructs a new {@link  MessagesConfig} and initialises {@code messages.yml}.
     *
     * @param plugin The instance of the plugin using this configuration.
     */
    public MessagesConfig(Plugin plugin) {
        super(plugin, "messages.yml");
    }

    /**
     * Retrieves a message from the configuration and applies colour codes.
     * <p>
     * If the path is not found, a warning is logged via {@link LoggerUtils}
     * and a placeholder error message is returned.
     * </p>
     *
     * @param path The configuration path to the message.
     * @return The colourised message, or a {@code &c} formatted error string if not found.
     */
    public String getMessage(@NotNull String path) {
        String message = config.getString(path);
        if (message == null) {
            LoggerUtils.warn("Message not found for path: " + path);
            return StringUtils.colour("&cMissing message: " + path);
        }
        return StringUtils.colour(message);
    }

    /**
     * Retrieves a colour-formatted message from the configuration.
     * <p>
     * This method is strict; if the path is missing or the value is not a string,
     * a {@link ConfigurationException} is thrown to ensure the configuration
     * integrity of the module.
     *
     * @param path The configuration path to the message.
     * @return The colour-translated message string.
     * @throws ConfigurationException If the path is missing or invalid.
     */
    @NotNull
    public String getMessagesStrict(@NotNull String path) {
        if (!config.contains(path)) throw new ConfigurationException(path, "String (Message)");
        if (!config.isString(path)) throw new ConfigurationException(path, "String (Message)");
        return StringUtils.colour(config.getString(path));
    }
}
