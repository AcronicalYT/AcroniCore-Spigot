package uk.acronical.exception.impl;

import org.jetbrains.annotations.NotNull;
import uk.acronical.exception.AcroniCoreException;

/**
 * Thrown when a required internal or external dependency fails to load,
 * is missing, or encounters a critical error during its setup.
 * <p>
 * This exception is utilised primarily during the initialisation phase of modules
 * to alert the developer of configuration or ordering issues.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class DependencyException extends AcroniCoreException {

    /**
     * Initialises a new dependency exception for a specific missing class.
     *
     * @param dependency The class that caused the failure.
     * @param reason     A descriptive reason for the failure.
     */
    public DependencyException(@NotNull Class<?> dependency, @NotNull String reason) {
        super(String.format("Dependency Failure | Class: %s | Reason: %s", dependency.getSimpleName(), reason));
    }

    /**
     * Initialises a new dependency exception for a class that encountered an internal error.
     *
     * @param dependency The class that caused the failure.
     * @param reason     A descriptive reason for the failure.
     * @param throwable  The underlying cause of the failure.
     */
    public DependencyException(@NotNull Class<?> dependency, @NotNull String reason, @NotNull Throwable throwable) {
        super(String.format("Dependency Failure | Class: %s | Reason: %s", dependency.getSimpleName(), reason), throwable);
    }
}