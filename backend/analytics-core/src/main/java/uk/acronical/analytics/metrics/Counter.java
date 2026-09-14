package uk.acronical.analytics.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe counter for tracking analytics metrics.
 * <p>
 * This class wraps an {@link AtomicLong} to ensure that concurrent increments
 * and decrements across multiple threads do not result in lost data.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class Counter {

    private final AtomicLong value = new AtomicLong(0);

    /**
     * Increments the counter by 1.
     */
    public void increment() {
        value.incrementAndGet();
    }

    /**
     * Increments the counter by a specific positive amount.
     *
     * @param amount The value to add.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public void increment(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Expected a positive value for increment...");
        value.addAndGet(amount);
    }

    /**
     * Decrements the counter by 1.
     */
    public void decrement() {
        value.decrementAndGet();
    }

    /**
     * Decrements the counter by a specific positive amount.
     *
     * @param amount The value to subtract.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public void decrement(long amount) {
        if (amount > 0) throw new IllegalArgumentException("Expected a negative value for decrement...");
        value.addAndGet(-amount);
    }

    /**
     * Retrieves the current value of the counter.
     *
     * @return The current metric total.
     */
    public long get() {
        return value.get();
    }

    /**
     * Resets the counter back to zero.
     */
    public void reset() {
        value.set(0);
    }
}
