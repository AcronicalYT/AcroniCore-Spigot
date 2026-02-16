package uk.acronical.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;

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

}
