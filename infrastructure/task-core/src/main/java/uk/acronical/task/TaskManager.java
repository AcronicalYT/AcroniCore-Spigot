package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class TaskManager {

    private final Plugin plugin;

    /**
     * Constructs a new TaskManager for the given plugin, allowing for the scheduling of synchronous and asynchronous tasks, as well as the creation of countdowns, task chains, and conditional tasks.
     *
     * @param plugin The plugin instance that will be used to schedule tasks on the server
     */
    public TaskManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if the TaskManager has been initialised with a plugin.
     * No tasks can run if the TaskManager has not been initialised, so this method can be used to check if the TaskManager is ready to use.
     *
     * @return true if the TaskManager has been initialised, false otherwise.
     */
    public boolean isInitialised() {
        return plugin != null;
    }

    /**
     * Runs a task synchronously on the main server thread.
     *
     * @param runnable The task to run.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask sync(Runnable runnable) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    /**
     * Runs a task synchronously on the main server thread after a delay.
     *
     * @param runnable The task to run.
     * @param delay The delay in ticks before the task is run.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask sync(Runnable runnable, long delay) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Runs a task synchronously on the main server thread after a delay and then repeatedly with a period.
     *
     * @param runnable The task to run.
     * @param delay The delay in ticks before the task is first run.
     * @param period The period in ticks between subsequent runs of the task.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask sync(Runnable runnable, long delay, long period) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Runs a task asynchronously on a separate thread.
     *
     * @param runnable The task to run.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask async(Runnable runnable) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Runs a task asynchronously on a separate thread after a delay.
     *
     * @param runnable The task to run.
     * @param delay The delay in ticks before the task is run.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask async(Runnable runnable, long delay) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Runs a task asynchronously on a separate thread after a delay and then repeatedly with a period.
     *
     * @param runnable The task to run.
     * @param delay The delay in ticks before the task is first run.
     * @param period The period in ticks between subsequent runs of the task.
     * @return A BukkitTask representing the task that was run.
     */
    public BukkitTask async(Runnable runnable, long delay, long period) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Creates a new CountdownBuilder instance.
     *
     * @return A new CountdownBuilder instance.
     */
    public CountdownBuilder newCountdown() {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new CountdownBuilder(plugin);
    }

    /**
     * Creates a new TaskChain instance.
     *
     * @return A new TaskChain instance.
     */
    public TaskChain newChain() {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new TaskChain(plugin);
    }

    /**
     * Creates a new ConditionalTask instance.
     *
     * @param task The task to run while the condition is true.
     * @param condition The condition that determines whether the task should continue running.
     * @param onStop The action to perform when the condition becomes false and the task is stopped.
     * @return A new ConditionalTask instance.
     */
    public ConditionalTask conditional(Runnable task, BooleanSupplier condition, Runnable onStop) {
        if (!isInitialised()) throw new IllegalStateException("TaskManager has not been initialised with a plugin.");
        return new ConditionalTask(task, condition, onStop);
    }

    /**
     * Creates a new CompletableFuture that runs a task asynchronously on a separate thread and completes with the result of the task.
     *
     * @param task The task to run.
     * @param <T> The type of the result of the task.
     * @return A CompletableFuture that will complete with the result of the task.
     */
    public <T> CompletableFuture<T> supplyAsync(Callable<T> task) {
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