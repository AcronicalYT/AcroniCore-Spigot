package uk.acronical.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.acronical.common.LoggerUtils;

import java.io.File;
import java.io.IOException;

public class DefaultConfig {

    protected final JavaPlugin plugin;
    protected FileConfiguration config;
    protected File file;
    protected final String fileName;

    /**
     * Constructor for the DefaultConfig class.
     *
     * @param plugin The instance of the main plugin.
     * @param fileName The name of the configuration file to load (e.g., "config.yml").
     */
    public DefaultConfig(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        createFile();
        reload();
    }

    /**
     * Creates the configuration file if it does not already exist. If the file does not exist, it attempts to create it by either copying a resource from the plugin's JAR or creating a new empty file. If an error occurs during file creation, it logs a severe message and prints the stack trace.
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
     * Reloads the configuration from the file, allowing any changes made to the file to be reflected in the plugin. If an error occurs during loading, it logs a severe message and prints the stack trace.
     */
    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the current state of the configuration to the file. If an error occurs during saving, it logs a severe message and prints the stack trace.
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
     * Gets the FileConfiguration object for this configuration file, allowing access to its contents.
     *
     * @return The FileConfiguration object representing the contents of this configuration file.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Gets a string value from the default configuration at the specified path. If the path does not exist or is not a string, it returns an empty string.
     *
     * @param path The path to the string value in the configuration.
     * @return The string value at the specified path, or an empty string if the path does not exist or is not a string.
     */
    public String getString(String path) {
        return config.getString(path, "");
    }

    /**
     * Gets a string value from the default configuration at the specified path. If the path does not exist or is not a string, it returns the provided default value.
     *
     * @param path The path to the string value in the configuration.
     * @param defaultValue The default value to return if the path does not exist or is not a string.
     * @return The string value at the specified path, or the provided default value if the path does not exist or is not a string.
     */
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    /**
     * Gets an integer value from the default configuration at the specified path. If the path does not exist or is not an integer, it returns 0.
     *
     * @param path The path to the integer value in the configuration.
     * @return The integer value at the specified path, or 0 if the path does not exist or is not an integer.
     */
    public int getInt(String path) {
        return config.getInt(path, 0);
    }

    /**
     * Gets an integer value from the default configuration at the specified path. If the path does not exist or is not an integer, it returns the provided default value.
     *
     * @param path The path to the integer value in the configuration.
     * @param defaultValue The default value to return if the path does not exist or is not an integer.
     * @return The integer value at the specified path, or the provided default value if the path does not exist or is not an integer.
     */
    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    /**
     * Gets a double value from the default configuration at the specified path. If the path does not exist or is not a double, it returns 0.0.
     *
     * @param path The path to the double value in the configuration.
     * @return The double value at the specified path, or 0.0 if the path does not exist or is not a double.
     */
    public double getDouble(String path) {
        return config.getDouble(path, 0.0);
    }

    /**
    * Gets a double value from the default configuration at the specified path. If the path does not exist or is not a double, it returns the provided default value.
     *
    * @param path The path to the double value in the configuration.
    * @param defaultValue The default value to return if the path does not exist or is not a double.
    * @return The double value at the specified path, or the provided default value if the path does not exist or is not a double.
    */
    public double getDouble(String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }

    /**
     * Gets a long value from the default configuration at the specified path. If the path does not exist or is not a long, it returns 0L.
     *
     * @param path The path to the long value in the configuration.
     * @return The long value at the specified path, or 0L if the path does not exist or is not a long.
     */
    public long getLong(String path) {
        return config.getLong(path, 0L);
    }

    /**
     * Gets a long value from the default configuration at the specified path. If the path does not exist or is not a long, it returns the provided default value.
     *
     * @param path The path to the long value in the configuration.
     * @param defaultValue The default value to return if the path does not exist or is not a long.
     * @return The long value at the specified path, or the provided default value if the path does not exist or is not a long.
     */
    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }

    /**
     * Gets a boolean value from the default configuration at the specified path. If the path does not exist or is not a boolean, it returns false.
     *
     * @param path The path to the boolean value in the configuration.
     * @param defaultValue The default value to return if the path does not exist or is not a boolean.
     * @return The boolean value at the specified path, or the provided default value if the path does not exist or is not a boolean.
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    /**
     * Gets a string array from the default configuration at the specified path. If the path does not exist or is not a list of strings, it returns an empty array.
     *
     * @param path The path to the list of strings in the configuration.
     * @return The string array at the specified path, or an empty array if the path does not exist or is not a list of strings.
     */
    public String[] getStringArray(String path) {
        return config.getStringList(path).toArray(new String[0]);
    }

    /**
     * Gets the integer array value from the default configuration at the specified path. If the path does not exist or is not a boolean, it returns false.
     *
     * @param path The path to the boolean value in the configuration.
     * @return The integer array value at the specified path, or false if the path does not exist or is not a boolean.
     */
    public int[] getIntArray(String path) {
        return config.getIntegerList(path).stream().mapToInt(i -> i).toArray();
    }

    /**
     * Gets a double array from the default configuration at the specified path. If the path does not exist or is not a list of doubles, it returns an empty array.
     *
     * @param path The path to the list of doubles in the configuration.
     * @return The double array at the specified path, or an empty array if the path does not exist or is not a list of doubles.
     */
    public double[] getDoubleArray(String path) {
        return config.getDoubleList(path).stream().mapToDouble(d -> d).toArray();
    }

}
