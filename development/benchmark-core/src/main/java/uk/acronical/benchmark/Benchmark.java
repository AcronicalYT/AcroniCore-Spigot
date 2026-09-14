package uk.acronical.benchmark;

import org.jetbrains.annotations.NotNull;

/**
 * A utility for profiling the performance and memory footprint of code execution.
 * <p>
 * This class includes JIT (Just-In-Time) warmup phases and garbage collection
 * requests to ensure benchmarks are as accurate as possible within the JVM.
 *
 * @author Acronical
 * @since 1.0.6
 */
public final class Benchmark {

    private Benchmark() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Executes a benchmark with a specific number of iterations and warmup cycles.
     *
     * @param label            The descriptive name of the benchmark.
     * @param iterations       The number of times to run the action for the actual test.
     * @param warmupIterations The number of times to run the action prior to testing.
     * @param action           The code block to profile.
     * @return The formatted benchmark result.
     */
    @NotNull
    public static BenchmarkResult run(@NotNull String label, int iterations, int warmupIterations, Runnable action) {
        if (iterations <= 0) throw new IllegalArgumentException("Iterations must be greater than 0");

        for (int i = 0; i < warmupIterations; i++) action.run();

        System.gc();
        Runtime runtime = Runtime.getRuntime();
        
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) action.run();
        long endTime = System.nanoTime();

        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryDelta = endMemory - startMemory;

        return new BenchmarkResult(label, iterations, endTime - startTime, memoryDelta);
    }

    /**
     * Executes a benchmark using the default 1,000 iterations and 100 warmup cycles.
     *
     * @param label  The descriptive name of the benchmark.
     * @param action The code block to profile.
     * @return The formatted benchmark result.
     */
    @NotNull
    public static BenchmarkResult run(@NotNull String label, @NotNull Runnable action) {
        return run(label, 1_000, 100, action);
    }
}
