package uk.acronical.reload;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * An immutable summary of a framework reload operation.
 * <p>
 * This result encapsulates the execution time, the number of successful
 * reloads, and any exceptions thrown by individual components, ensuring
 * developers have complete visibility into the reload cycle.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class ReloadResult {

    private final long elapsedNanos;
    private final Map<String, Throwable> failures;
    private final int totalCount;

    /**
     * Initialises a new reload result summary.
     *
     * @param elapsedNanos The total time taken for the reload sequence in nanoseconds.
     * @param failures     A map of component names to the exceptions they threw.
     * @param totalCount   The total number of components attempted.
     */
    public ReloadResult(long elapsedNanos, @NotNull Map<String, Throwable> failures, int totalCount) {
        this.elapsedNanos = elapsedNanos;
        this.failures = Collections.unmodifiableMap(failures);
        this.totalCount = Math.max(0, totalCount);
    }

    /**
     * Retrieves the total time taken in milliseconds.
     *
     * @return The elapsed time in milliseconds.
     */
    public double getElapsedMillis() {
        return elapsedNanos / 1_000_000.0;
    }

    /**
     * Checks if the entire reload sequence completed without any exceptions.
     *
     * @return True if all components reloaded successfully, false otherwise.
     */
    public boolean isSuccessful() {
        return failures.isEmpty();
    }

    /**
     * Retrieves the total number of components that were scheduled for reload.
     *
     * @return The total component count.
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Retrieves the number of components that reloaded successfully.
     *
     * @return The success count.
     */
    public int getSuccessCount() {
        return totalCount - failures.size();
    }

    /**
     * Retrieves an unmodifiable map of the failures encountered.
     *
     * @return A map where the key is the component's reload name, and the value is the thrown exception.
     */
    @NotNull
    public Map<String, Throwable> getFailures() {
        return failures;
    }
}
