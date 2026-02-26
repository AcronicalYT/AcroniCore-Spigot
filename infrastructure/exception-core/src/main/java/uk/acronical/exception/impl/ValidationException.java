package uk.acronical.exception.impl;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when data fails a logical integrity check or business rule.
 * <p>
 * This exception is utilised to signal that provided input—while perhaps
 * syntactically correct—violates the specific constraints of the framework
 * or a module (e.g., a negative price or an out-of-bounds index).
 *
 * @author Acronical
 * @since 1.0.5
 */
public class ValidationException extends AcroniCoreException {

    /**
     * Initialises a new validation exception with a specific failure message.
     *
     * @param message The descriptive reason for the validation failure.
     */
    public ValidationException(@NotNull String message) {
        super("Validation Failure | " + message);
    }

    /**
     * Initialises a new validation exception with a message and an underlying cause.
     *
     * @param message The descriptive reason for the validation failure.
     * @param cause   The underlying cause of the failure.
     */
    public ValidationException(@NotNull String message, @NotNull Throwable cause) {
        super("Validation Failure | " + message, cause);
    }
}