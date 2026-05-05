package uk.acronical.analytics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.analytics.metrics.Counter;
import uk.acronical.analytics.metrics.Gauge;
import uk.acronical.common.LoggerUtils;
import uk.acronical.nms.PerformanceFactory;
import uk.acronical.task.TaskManager;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;

/**
 * The central registry and service for tracking framework analytics.
 * <p>
 * This service manages all active {@link Counter} and {@link Gauge} metrics,
 * automatically tracking core server vitals upon initialisation.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class AnalyticsService {

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();

    /**
     * Initialises the Analytics Service and registers default server vitals.
     *
     * @param plugin      The core plugin instance.
     * @param taskManager The task manager for scheduled metric reporting.
     */
    public AnalyticsService(@NotNull Plugin plugin, @NotNull TaskManager taskManager) {
        registerServerVitals();
    }

    /**
     * Retrieves an existing counter or creates a new one if it does not exist.
     *
     * @param metricName The unique identifier for the metric.
     * @return The thread-safe counter.
     */
    @NotNull
    public Counter getCounter(@NotNull String metricName) {
        return counters.computeIfAbsent(metricName, key -> new Counter());
    }

    /**
     * Registers a new dynamically calculated gauge metric.
     *
     * @param metricName    The unique identifier for the metric.
     * @param valueSupplier The function providing the instantaneous value.
     */
    public void registerGauge(@NotNull String metricName, @NotNull DoubleSupplier valueSupplier) {
        gauges.put(metricName, new Gauge(valueSupplier));
    }

    private void registerServerVitals() {
        registerGauge("server.players.online", () -> (double) Bukkit.getOnlinePlayers().size());
        registerGauge("server.performance.tps_1m", () -> Math.min(20.0, PerformanceFactory.getRecentTps()[0]));

        Runtime runtime = Runtime.getRuntime();
        registerGauge("server.memory.used_mb", () -> (runtime.totalMemory() - runtime.freeMemory()) / 1048576.0);
        registerGauge("server.memory.max_mb", () -> runtime.maxMemory() / 1048576.0);
    }

    /**
     * @return An unmodifiable view of all registered counters.
     */
    @NotNull
    public Map<String, Counter> getAllCounters() {
        return Collections.unmodifiableMap(counters);
    }

    /**
     * @return An unmodifiable view of all registered gauges.
     */
    @NotNull
    public Map<String, Gauge> getAllGauges() {
        return Collections.unmodifiableMap(gauges);
    }

    /**
     * Dumps all current metric values to the server console.
     */
    public void dumpMetricsToConsole() {
        LoggerUtils.info("=== Analytics Dump ===");
        gauges.forEach((name, gauge) -> LoggerUtils.info(String.format("[Gauge] %s : %.2f", name, gauge.get())));
        counters.forEach((name, counter) -> LoggerUtils.info(String.format("[Counter] %s : %d", name, counter.get())));
        LoggerUtils.info("=================================");
    }
}
