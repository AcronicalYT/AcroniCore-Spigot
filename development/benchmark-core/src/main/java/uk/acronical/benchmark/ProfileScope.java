package uk.acronical.benchmark;

import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.util.function.Consumer;

/**
 * A utility for profiling the execution time of specific code blocks.
 * <p>
 * Designed to be utilised within a try-with-resources block, this scope
 * automatically records the start time upon initialisation and reports the
 * elapsed duration when the block closes.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class ProfileScope implements AutoCloseable {

    private final String label;
    private final long startNanos;
    private final Consumer<String> outputHandler;

    /**
     * Initialises a new profiling scope with a custom output handler.
     *
     * @param label         The descriptive name of the profiled block.
     * @param outputHandler The consumer that processes the formatted result.
     */
    public ProfileScope(@NotNull String label, @NotNull Consumer<String> outputHandler) {
        this.label = label;
        this.startNanos = System.nanoTime();
        this.outputHandler = outputHandler;
    }

    /**
     * Initialises a new profiling scope with a debug log option.
     *
     * @param label    The descriptive name of the profiled block.
     * @param debugLog Whether to use debug logging.
     */
    public ProfileScope(@NotNull String label, boolean debugLog) {
        this(label, debugLog ? LoggerUtils::debug : LoggerUtils::info);
    }

    /**
     * Initialises a new profiling scope, logging the result to the framework logger.
     *
     * @param label The descriptive name of the profiled block.
     */
    public ProfileScope(@NotNull String label) {
        this(label, LoggerUtils::info);
    }

    @Override
    public void close() {
        long elapsedNanos = System.nanoTime() - startNanos;
        double elapsedMillis = elapsedNanos / 1_000_000.0;
        outputHandler.accept(String.format("§8[Profile] §e%s §7took §a%.3fms", label, elapsedMillis));
    }
}
