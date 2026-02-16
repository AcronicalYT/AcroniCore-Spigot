package uk.acronical.common;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * A utility class for standardised plugin logging.
 * <p>
 * This class provides a wrapper around the {@link java.util.logging.Logger},
 * automatically applying prefixes and colour translation to all console messages.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class LoggerUtils {

    private static Plugin plugin;
    private static boolean debugMode = false;
    private static String prefix = "";

    /**
     * Initialises the {@link LoggerUtils} with a plugin instance.
     * <p>
     * This setup uses the default prefix format: {@code [PluginName]}.
     *
     * @param instance The {@link Plugin} instance to use for logging.
     */
    public static void init(@NotNull Plugin instance) {
        plugin = instance;
        prefix = "[" + instance.getName() + "] ";
    }

    /**
     * Initialises the {@link LoggerUtils} with a plugin instance and a custom prefix.
     *
     * @param instance        The {@link Plugin} instance to use for logging.
     * @param preferredPrefix A custom string to prepend to all log messages.
     */
    public static void init(@NotNull Plugin instance, @NotNull String preferredPrefix) {
        plugin = instance;
        prefix = preferredPrefix;
    }

    /**
     * Toggles the debug mode for the logger.
     * <p>
     * When enabled, messages sent via {@link #debug(String)} will be processed.
     *
     * @param status {@code true} to enable debug output, {@code false} to disable.
     */
    public static void setDebugMode(boolean status) {
        debugMode = status;
    }

    /**
     * Logs an informational message at the {@link Level#INFO} level.
     *
     * @param message The message to log.
     */
    public static void info(@NotNull String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a warning message at the {@link Level#WARNING} level.
     *
     * @param message The message to log.
     */
    public static void warn(@NotNull String message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs a severe error message at the {@link Level#SEVERE} level.
     *
     * @param message The message to log.
     */
    public static void severe(@NotNull String message) {
        log(Level.SEVERE, message);
    }

    /**
     * Logs a debug message if debug mode is currently enabled.
     * <p>
     * Debug messages are logged at the {@link Level#FINE} level.
     *
     * @param message The message to log.
     */
    public static void debug(@NotNull String message) {
        if (plugin != null && debugMode) {
            log(Level.FINE, message);
        }
    }

    /**
     * Dispatches a message to the console with a specific {@link Level}.
     * <p>
     * The message is automatically prefixed and colour-translated via
     * {@link StringUtils#colour(String)}.
     *
     * @param level   The {@link Level} of the log entry.
     * @param message The message to log.
     * @throws IllegalStateException If the utility has not been initialised.
     */
    public static void log(@NotNull Level level, @NotNull String message) {
        if (plugin == null) {
            throw new IllegalStateException("LoggerUtils has not been initialised. Call LoggerUtils.init() with a plugin instance before using this method.");
        }
        plugin.getLogger().log(level, StringUtils.colour(prefix + message));
    }
}