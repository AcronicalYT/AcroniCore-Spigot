package uk.acronical.exception.impl;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when a configuration value is missing, malformed, or of an incorrect type.
 * <p>
 * This exception is utilised to provide clear feedback to the developer or
 * administrator regarding specific errors within configuration files (e.g., YAML or JSON).
 *
 * @author Acronical
 * @since 1.0.5
 */
public class ConfigurationException extends AcroniCoreException {

    /**
     * Initialises a new configuration exception for a type mismatch or missing value.
     *
     * @param path         The configuration path (e.g., "database.port").
     * @param expectedType The expected data type (e.g., "Integer" or "Boolean").
     */
    public ConfigurationException(@NotNull String path, @NotNull String expectedType) {
        super(String.format("Config Mismatch | Path: [%s] | Expected Type: %s", path, expectedType));
    }

    /**
     * Initialises a new configuration exception with an underlying cause.
     *
     * @param path         The configuration path.
     * @param expectedType The expected data type.
     * @param throwable    The underlying cause (e.g., a number format exception).
     */
    public ConfigurationException(@NotNull String path, @NotNull String expectedType, @NotNull Throwable throwable) {
        super(String.format("Config Mismatch | Path: [%s] | Expected Type: %s", path, expectedType), throwable);
    }
}