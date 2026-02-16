package uk.acronical.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.io.File;
import java.io.IOException;

/**
 * Handles creation, loading, and saving of YAML configuration files.
 * <p>
 * This wrapper provides convenience methods for retrieving data with
 * guaranteed default values to prevent NullPointerExceptions.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class DefaultConfig {

    protected final Plugin plugin;
    protected FileConfiguration config;
    protected File file;
    protected final String fileName;

    /**
     * Constructor for the {@link DefaultConfig} class.
     *
     * @param plugin The instance of the main plugin.
     * @param fileName The name of the configuration file to load (e.g., {@code config.yml}).
     */
    public DefaultConfig(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        createFile();
        reload();
    }

    /**
     * Initialises the configuration file on the server.
     * <p>
     * If the file does not exist, it attempts to copy a default version from the
     * plugin's resources. If no resource is found, an empty file is created.
     * <p>
     * Errors during file creation are logged as severe via {@link LoggerUtils}.
     */
    private void createFile() {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    LoggerUtils.severe("Could not create configuration file: " + fileName);
                    LoggerUtils.severe(e.getMessage());
                }
            }
        }
    }

    /**
     * Reloads the configuration from the physical file.
     * <p>
     * This updates the internal {@link FileConfiguration} object with the current contents on disk.
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the current in-memory configuration to the physical file.
     * <p>
     * Errors during saving are logged as severe via {@link LoggerUtils}.
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            LoggerUtils.severe("Could not save configuration file: " + fileName);
            LoggerUtils.severe(e.getMessage());
        }
    }

    /**
     * @return The underlying {@link FileConfiguration} object.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Retrieves a string value, returning an empty string if not found.
     *
     * @param path The configuration path.
     * @return The string at the path, or {@code ""} if missing/invalid.
     */
    public String getString(@NotNull String path) {
        return config.getString(path, "");
    }

    /**
     * Retrieves a string value, returning {@code defaultValue} if not found.
     *
     * @param path The configuration path.
     * @param defaultValue The default value to return if the path does not exist or is not a string.
     * @return The string at the path, or {@code defaultValue} if missing/invalid.
     */
    public String getString(@NotNull String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    /**
     * Retrieves an integer value, returning 0 if not found or invalid.
     *
     * @param path The configuration path.
     * @return The integer at the path, or {@code 0} if missing/invalid.
     */
    public int getInt(@NotNull String path) {
        return config.getInt(path, 0);
    }

    /**
     * Retrieves an integer value, returning {@code defaultValue} if not found or invalid.
     *
     * @param path The configuration path.
     * @param defaultValue The default value to return if the path does not exist or is not an integer.
     * @return The integer at the path, {@code defaultValue} if missing/invalid.
     */
    public int getInt(@NotNull String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    /**
     * Retrieves a double value, returning 0.0 if not found or invalid.
     *
     * @param path The configuration path.
     * @return The double at the path, or {@code 0.0} if missing/invalid.
     */
    public double getDouble(@NotNull String path) {
        return config.getDouble(path, 0.0);
    }

    /**
     * Retrieves a double value, returning {@code defaultValue} if not found or invalid.
     *
     * @param path The configuration path.
     * @param defaultValue The default value to return if the path does not exist or is not a double.
     * @return The double at the path, or {@code defaultValue} if missing/invalid.
     */
    public double getDouble(@NotNull String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }

    /**
     * Retrieves a long value, returning 0L if not found or invalid.
     *
     * @param path The configuration path.
     * @return The long at the path, or {@code 0L} if missing/invalid.
     */
    public long getLong(@NotNull String path) {
        return config.getLong(path, 0L);
    }

    /**
     * Retrieves a long value, returning {@code defaultValue} if not found or invalid.
     *
     * @param path The configuration path.
     * @param defaultValue The default value to return if the path does not exist or is not a long.
     * @return The long at the path, or {@code defaultValue} if missing/invalid.
     */
    public long getLong(@NotNull String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }

    /**
     * Retrieves a boolean value, returning false if not found or invalid.
     *
     * @param path The configuration path.
     * @return The boolean at the path, or {@code false} if missing/invalid.
     */
    public boolean getBoolean(@NotNull String path) {
        return config.getBoolean(path, false);
    }

    /**
     * Retrieves a boolean value, returning {@code defaultValue} if not found or invalid.
     *
     * @param path The configuration path.
     * @param defaultValue The default value to return if the path does not exist or is not a boolean.
     * @return The boolean at the path, or {@code defaultValue} if missing/invalid.
     */
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    /**
     * Retrieves a string array, returning an empty array if the path does not exist or is not a list of strings.
     *
     * @param path The configuration path.
     * @return The string array at the specified path, or an empty array if the path does not exist or is not a list of strings.
     */
    public String[] getStringArray(@NotNull String path) {
        return config.getStringList(path).toArray(new String[0]);
    }

    /**
     * Retrieves an integer array, returning an empty array if the path does not exist or is not a list of integers.
     *
     * @param path The configuration path.
     * @return The integer array at the specified path, or an empty array if the path does not exist or is not a list of integers.
     */
    public int[] getIntArray(@NotNull String path) {
        return config.getIntegerList(path).stream().mapToInt(i -> i).toArray();
    }

    /**
     * Retrieves a double array, returning an empty array if the path does not exist or is not a list of doubles.
     *
     * @param path The configuration path.
     * @return The double array at the specified path, or an empty array if the path does not exist or is not a list of doubles.
     */
    public double[] getDoubleArray(@NotNull String path) {
        return config.getDoubleList(path).stream().mapToDouble(d -> d).toArray();
    }
}