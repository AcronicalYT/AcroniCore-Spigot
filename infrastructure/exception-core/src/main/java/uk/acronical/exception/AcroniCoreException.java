package uk.acronical.exception;

import org.jetbrains.annotations.NotNull;

/**
 * The base exception class for all internal framework errors within AcroniCore.
 * <p>
 * This extends {@link RuntimeException} to allow for unchecked error propagation
 * while maintaining a consistent logging format across all modules.
 *
 * @author Acronical
 * @since 1.0.5
 */
public abstract class AcroniCoreException extends RuntimeException {

    /**
     * Initialises a new exception with a prefixed reason.
     *
     * @param reason The descriptive error reason.
     */
    public AcroniCoreException(@NotNull String reason) {
        super("[AcroniCore] " + reason);
    }

    /**
     * Initialises a new exception with a prefixed reason and a cause.
     *
     * @param reason   The descriptive error reason.
     * @param throwable The underlying cause of the exception.
     */
    public AcroniCoreException(@NotNull String reason, @NotNull Throwable throwable) {
        super("[AcroniCore] " + reason, throwable);
    }
}
