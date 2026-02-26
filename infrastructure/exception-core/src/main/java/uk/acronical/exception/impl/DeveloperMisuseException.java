package uk.acronical.exception.impl;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when the framework's API is utilised in a way that violates its contract.
 * <p>
 * This exception is designed to alert developers during the development phase
 * regarding improper method calls, invalid arguments, or logic errors that
 * contradict the framework's intended usage.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class DeveloperMisuseException extends AcroniCoreException {

    /**
     * Initialises a new misuse exception with a descriptive message.
     *
     * @param message The explanation of the API violation.
     */
    public DeveloperMisuseException(@NotNull String message) {
        super("API Contract Violation | " + message);
    }

    /**
     * Initialises a new misuse exception with a message and an underlying cause.
     *
     * @param message The explanation of the API violation.
     * @param cause   The underlying cause of the misuse.
     */
    public DeveloperMisuseException(@NotNull String message, @NotNull Throwable cause) {
        super("API Contract Violation | " + message, cause);
    }
}