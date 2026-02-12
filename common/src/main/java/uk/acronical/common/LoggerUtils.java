package uk.acronical.common;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class LoggerUtils {

    private static Plugin plugin;
    private static boolean debugMode = false;
    private static String prefix = "";

    /**
     * Initialise the LoggerUtils with only the plugin instance.
     * This will use the default prefix instance name for log messages. (the plugin name in square brackets)
     *
     * @param instance The plugin instance to use for logging.
     */
    public static void init(Plugin instance) {
        plugin = instance;
        prefix = "[" + instance.getName() + "] ";
    }

    /**
     * Initialise the LoggerUtils with the plugin instance and a prefix for log messages.
     * You only need to use this method to set the prefix if you want to use a custom prefix for your log messages.
     *
     * @param instance The plugin instance to use for logging.
     * @param preferredPrefix A string prefix to prepend to all log messages.
     */
    public static void init(Plugin instance, String preferredPrefix) {
        plugin = instance;
        prefix = preferredPrefix;
    }

    /**
     * Set the debug mode for the logger. When debug mode is enabled, debug messages will be logged to the console.
     *
     * @param status True to enable debug mode, false to disable it.
     */
    public static void setDebugMode(boolean status) {
        debugMode = status;
    }

    /**
     * Log an informational message to the console. Informational messages are logged at the INFO level.
     *
     * @param message The informational message to log.
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log a warning message to the console. Warning messages are logged at the WARNING level.
     *
     * @param message The warning message to log.
     */
    public static void warn(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Log a severe error message to the console. Severe messages are logged at the SEVERE level.
     *
     * @param message The severe error message to log.
     */
    public static void severe(String message) {
        log(Level.SEVERE, message);
    }

    /**
     * Log a debug message to the console if debug mode is enabled. Debug messages are logged at the FINE level.
     *
     * @param message The debug message to log.
     */
    public static void debug(String message) {
        if (plugin != null && debugMode) {
            log(Level.FINE, message);
        }
    }

    /**
     * Log a message to the console with the specified log level.
     *
     * @param level The log level to use (e.g., INFO, WARNING, SEVERE).
     * @param message The message to log.
     */
    public static void log(Level level, String message) {
        if (plugin == null) {
            throw new IllegalStateException("LoggerUtils has not been initialized. Call LoggerUtils.init() with a plugin instance before using this method.");
        }
        plugin.getLogger().log(level, StringUtils.colour(prefix + message));
    }
}