package uk.acronical.config.exception;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when a low-level file system error occurs during configuration handling.
 * <p>
 * This exception is utilised to wrap {@link java.io.IOException} occurrences
 * during specific actions such as "Saving", "Loading", or "Initialising"
 * configuration files.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class ConfigIOException extends AcroniCoreException {

    /**
     * Initialises a new configuration I/O exception with an underlying cause.
     *
     * @param fileName The name of the file being accessed (e.g., "config.yml").
     * @param action   The operation being performed (e.g., "Saving" or "Loading").
     * @param cause    The underlying {@link Throwable} (typically an {@link java.io.IOException}).
     */
    public ConfigIOException(@NotNull String fileName, @NotNull String action, @NotNull Throwable cause) {
        super(String.format("Config I/O Error | File: [%s] | Action: %s", fileName, action), cause);
    }
}