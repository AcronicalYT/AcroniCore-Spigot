package uk.acronical.exception.impl;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when the framework is in an inappropriate state for the requested operation.
 * <p>
 * This exception is utilised to prevent illegal lifecycle transitions, such as
 * modifying a locked configuration or accessing a service after it has been
 * shutdown.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class IllegalFrameworkStateException extends AcroniCoreException {

    /**
     * Initialises a new framework state exception with a specific message.
     *
     * @param message The descriptive error message.
     */
    public IllegalFrameworkStateException(@NotNull String message) {
        super("Lifecycle Violation | State Error: " + message);
    }

    /**
     * Initialises a new framework state exception with a message and a cause.
     *
     * @param message   The descriptive error message.
     * @param throwable The underlying cause of the state failure.
     */
    public IllegalFrameworkStateException(@NotNull String message, @NotNull Throwable throwable) {
        super("Lifecycle Violation | State Error: " + message, throwable);
    }
}