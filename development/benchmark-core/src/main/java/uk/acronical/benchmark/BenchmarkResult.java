package uk.acronical.benchmark;

import org.jetbrains.annotations.NotNull;

/**
 * An immutable data model representing the results of a performance benchmark.
 * <p>
 * This class encapsulates execution time and memory allocation metrics, providing
 * safe mathematical conversions for reporting framework performance.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class BenchmarkResult {

    private final String label;
    private final int iterations;
    private final long totalNanos;
    private final long memoryUsedBytes;

    /**
     * Initialises a new benchmark result.
     *
     * @param label           The descriptive name of the benchmark.
     * @param iterations      The number of times the target code was executed.
     * @param totalNanos      The total execution time across all iterations in nanoseconds.
     * @param memoryUsedBytes The estimated heap memory allocated during execution.
     */
    public BenchmarkResult(@NotNull String label, int iterations, long totalNanos, long memoryUsedBytes) {
        this.label = label;
        this.iterations = Math.max(0, iterations);
        this.totalNanos = totalNanos;
        this.memoryUsedBytes = memoryUsedBytes;
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    public int getIterations() {
        return iterations;
    }

    public long getTotalNanos() {
        return totalNanos;
    }

    public double getTotalMillis() {
        return (double) totalNanos / 1_000_000.0;
    }

    /**
     * Calculates the average execution time per iteration in nanoseconds.
     *
     * @return The average time, or 0.0 if no iterations were run.
     */
    public double getAverageTimeNanos() {
        if (iterations == 0) return 0.0;
        return (double) totalNanos / iterations;
    }

    /**
     * Calculates the average execution time per iteration in milliseconds.
     *
     * @return The average time, or 0.0 if no iterations were run.
     */
    public double getAverageTimeMillis() {
        return getAverageTimeNanos() / 1_000_000.0;
    }

    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public double getMemoryUsedKilobytes() {
        return (double) memoryUsedBytes / 1024.0;
    }

    @Override
    @NotNull
    public String toString() {
        return String.format(
            "§7[Benchmark §e%s§7] Runs: §f%d §8| §7Total: §e%.3fms §8| §7Avg: §a%.4fms §8| §7Heap: §b%.2f KB",
            label, iterations, getTotalMillis(), getAverageTimeMillis(), getMemoryUsedKilobytes()
        );
    }
}
