package uk.acronical.analytics.metrics;

import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleSupplier;

/**
 * A metric that represents a single numerical value that can arbitrarily go up and down.
 * <p>
 * Unlike a counter, a Gauge is evaluated on-demand using a provided function.
 * This makes it ideal for tracking current states such as memory usage,
 * active player counts, or server performance.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class Gauge {

    private final DoubleSupplier valueProvider;

    /**
     * Initialises a new gauge with the specified value provider.
     *
     * @param valueProvider The function used to dynamically calculate the current value.
     */
    public Gauge(@NotNull DoubleSupplier valueProvider) {
        this.valueProvider = valueProvider;
    }

    /**
     * Retrieves the current value of the gauge.
     *
     * @return The instantaneous metric value.
     */
    public double get() {
        return valueProvider.getAsDouble();
    }
}
