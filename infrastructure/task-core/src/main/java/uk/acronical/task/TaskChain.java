package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A utility for sequencing synchronous and asynchronous tasks with optional delays.
 * <p>
 * This class facilitates a "fluent" approach to task scheduling, allowing
 * developers to chain operations together without nested callbacks.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class TaskChain {

    private final Plugin plugin;
    private final Queue<ChainLink> chainLink = new LinkedList<>();
    private final BukkitScheduler scheduler;

    /**
     * Initialises a new {@link TaskChain} for the specified {@link Plugin}.
     *
     * @param plugin The plugin instance used to schedule tasks.
     */
    public TaskChain(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Appends a synchronous task to the chain.
     * <p>
     * Synchronous tasks run on the main server thread and are safe for
     * interacting with the Bukkit API and Minecraft world state.
     *
     * @param task The logic to execute.
     * @return The current {@link TaskChain} instance for method chaining.
     */
    public TaskChain sync(@NotNull Runnable task) {
        chainLink.add(new ChainLink(task, true, 0));
        return this;
    }

    /**
     * Appends an asynchronous task to the chain.
     * <p>
     * Asynchronous tasks run on a separate thread pool. This is ideal for
     * blocking operations such as database queries or network requests.
     *
     * @param task The logic to execute.
     * @return The current {@link TaskChain} instance for method chaining.
     */
    public TaskChain async(@NotNull Runnable task) {
        chainLink.add(new ChainLink(task, false, 0));
        return this;
    }

    /**
     * Introduces a pause in the execution chain.
     *
     * @param ticks The duration to wait before the next link, measured in server ticks.
     * @return The current {@link TaskChain} instance for method chaining.
     */
    public TaskChain delay(long ticks) {
        chainLink.add(new ChainLink(null, true, ticks));
        return this;
    }

    /**
     * Starts the execution of the chain.
     * <p>
     * Tasks are processed sequentially in the order they were added. Note that
     * this method consumes the internal queue; the chain cannot be re-executed.
     */
    public void execute() {
        runNextLink();
    }

    /**
     * Polls the next link from the queue and determines the execution timing.
     */
    private void runNextLink() {
        ChainLink link = chainLink.poll();

        if (link == null) return;

        if (link.delay > 0) scheduler.runTaskLater(plugin, () -> processLink(link), link.delay);
        else processLink(link);
    }

    /**
     * Processes a single {@link ChainLink}, handling thread context and error catching.
     *
     * @param link The link to process.
     */
    private void processLink(ChainLink link) {
        if (link.task == null) {
            runNextLink();
            return;
        }

        Runnable execution = () -> {
            try {
                link.task.run();
            } catch (Exception e) {
                LoggerUtils.severe("[TaskChain] Error in chain: " + e.getMessage());
            } finally {
                runNextLink();
            }
        };

        if (link.sync) scheduler.runTask(plugin, execution);
        else scheduler.runTaskAsynchronously(plugin, execution);
    }

    /**
     * A record representing an individual segment of the execution chain.
     *
     * @param task  The {@link Runnable} to execute (may be null for delays).
     * @param sync  Whether the task requires the main server thread.
     * @param delay The delay in ticks before proceeding to the next link.
     */
    private record ChainLink(Runnable task, boolean sync, long delay) {}
}
