package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A central management class for scheduling and managing plugin tasks.
 * <p>
 * This class acts as a wrapper for the {@link org.bukkit.scheduler.BukkitScheduler},
 * providing a simplified API for synchronous and asynchronous execution, as well
 * as access to specialised utilities like {@link TaskChain} and {@link CountdownBuilder}.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class TaskManager {

    private final Plugin plugin;

    /**
     * Initialises a new {@link TaskManager} for the specified {@link Plugin}.
     *
     * @param plugin The plugin instance used to schedule tasks.
     */
    public TaskManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Verifies if the manager is ready to schedule tasks.
     *
     * @return {@code true} if initialised with a valid plugin; otherwise {@code false}.
     */
    public boolean isInitialised() {
        return plugin != null;
    }

    /**
     * Executes a task synchronously on the main server thread.
     *
     * @param runnable The logic to execute.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask sync(@NotNull Runnable runnable) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    /**
     * Executes a task synchronously after a specified delay.
     *
     * @param runnable The logic to execute.
     * @param delay    The wait time in server ticks.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask sync(@NotNull Runnable runnable, long delay) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Executes a repeating synchronous task.
     *
     * @param runnable The logic to execute.
     * @param delay    The initial wait time in ticks.
     * @param period   The interval between executions in ticks.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask sync(@NotNull Runnable runnable, long delay, long period) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Executes a task asynchronously on a separate thread pool.
     *
     * @param runnable The logic to execute.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask async(@NotNull Runnable runnable) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Executes a task asynchronously after a specified delay.
     *
     * @param runnable The logic to execute.
     * @param delay    The wait time in server ticks.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask async(@NotNull Runnable runnable, long delay) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Executes a repeating asynchronous task.
     *
     * @param runnable The logic to execute.
     * @param delay    The initial wait time in ticks.
     * @param period   The interval between executions in ticks.
     * @return The resulting {@link BukkitTask}.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public BukkitTask async(@NotNull Runnable runnable, long delay, long period) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Creates a new {@link CountdownBuilder} instance.
     *
     * @return A fresh builder for second-based countdowns.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public CountdownBuilder newCountdown() {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new CountdownBuilder(plugin);
    }

    /**
     * Creates a new {@link TaskChain} instance.
     *
     * @return A fresh chain for sequencing multi-threaded operations.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public TaskChain newChain() {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new TaskChain(plugin);
    }

    /**
     * Initialises a {@link ConditionalTask}.
     *
     * @param task      The logic to run while the condition is met.
     * @param condition The requirement to check before each run.
     * @param onStop    The termination logic (may be {@code null}).
     * @return A new {@link ConditionalTask} instance.
     * @throws IllegalStateException If the manager is not initialised.
     */
    public ConditionalTask conditional(@NotNull Runnable task, @NotNull BooleanSupplier condition, Runnable onStop) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new ConditionalTask(task, condition, onStop);
    }

    /**
     * Executes a task asynchronously and returns its result via a {@link CompletableFuture}.
     * <p>
     * This utility bridges {@link Callable} logic with asynchronous execution,
     * handling errors and completing the future exceptionally if the task fails.
     *
     * @param <T>  The type of result produced.
     * @param task The logic to execute.
     * @return A future that will complete with the task's result.
     */
    public <T> CompletableFuture<T> supplyAsync(@NotNull Callable<T> task) {
        CompletableFuture<T> future = new CompletableFuture<>();
        async(() -> {
            try {
                T result = task.call();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}