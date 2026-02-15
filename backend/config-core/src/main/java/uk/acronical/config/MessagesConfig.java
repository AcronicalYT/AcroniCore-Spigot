package uk.acronical.config;

import org.bukkit.plugin.java.JavaPlugin;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;

public class MessagesConfig extends DefaultConfig {

    /**
     * Constructor for the MessagesConfig class, which extends the DefaultConfig class. This constructor initialises the MessagesConfig by calling the superclass constructor
     *
     * @param plugin The instance of the JavaPlugin that is using this configuration. This parameter is passed to the superclass constructor
     */
    public MessagesConfig(JavaPlugin plugin) {
        super(plugin, "messages.yml");
    }

    /**
     * Retrieves a message from the configuration file based on the provided path. If the message is not found, it logs a warning and returns a default message indicating that the message is missing.
     *
     * @param path The path in the configuration file where the desired message is located. This should be a string that corresponds to the key in the YAML file for the message you want to retrieve.
     * @return The message retrieved from the configuration file. If the message is not found, it returns a default message indicating that the message is missing, formatted with color codes.
     */
    public String getMessage(String path) {
        String message = config.getString(path);
        if (message == null) {
            LoggerUtils.warn("Message not found for path: " + path);
            return StringUtils.colour("&cMissing message: " + path);
        }
        return StringUtils.colour(message);
    }

}
