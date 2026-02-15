package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import uk.acronical.common.LoggerUtils;

import java.util.LinkedList;
import java.util.Queue;

public class TaskChain {

    private final Plugin plugin;
    private final Queue<ChainLink> chainLink = new LinkedList<>();
    private final BukkitScheduler scheduler;

    /**
     * Constructs a new TaskChain for the given plugin, allowing for the chaining of synchronous and asynchronous tasks with optional delays.
     *
     * @param plugin The plugin instance that will be used to schedule tasks on the server
     */
    public TaskChain(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    /**
     * Adds a task to the chain that will run synchronously on the main server thread.
     * Useful for tasks that need to interact with the Minecraft server or Bukkit API, as these operations must be performed on the main thread to avoid concurrency issues.
     *
     * @param task The task to run.
     * @return The TaskChain instance, allowing for method chaining.
     */
    public TaskChain sync(Runnable task) {
        chainLink.add(new ChainLink(task, true, 0));
        return this;
    }

    /**
     * Adds a task to the chain that will run asynchronously on a separate thread.
     * Useful for tasks that may take a long time to complete and would otherwise block the main server thread, such as database operations or network requests.
     *
     * @param task The task to run.
     * @return The TaskChain instance, allowing for method chaining.
     */
    public TaskChain async(Runnable task) {
        chainLink.add(new ChainLink(task, false, 0));
        return this;
    }

    /**
     * Adds a delay to the chain, causing the next task to run after a specified number of ticks.
     *
     * @param ticks The delay in ticks before the next task is run.
     * @return The TaskChain instance, allowing for method chaining.
     */
    public TaskChain delay(long ticks) {
        chainLink.add(new ChainLink(null, true, ticks));
        return this;
    }

    /**
     * Executes the tasks in the chain in the order they were added, respecting the specified delays and execution contexts (synchronous or asynchronous).
     * This method should be called after all desired tasks and delays have been added to the chain.
     */
    public void execute() {
        runNextLink();
    }

    private void runNextLink() {
        ChainLink link = chainLink.poll();

        if (link == null) return;

        if (link.delay > 0) scheduler.runTaskLater(plugin, () -> processLink(link), link.delay);
        else processLink(link);
    }

    /**
     * Processes a single link in the chain, executing the task if it exists and then proceeding to the next link.
     *
     * @param link The ChainLink to process, which may contain a task to run and a delay before the next task is executed.
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
     * A record representing a single link in the task chain, containing the task to execute, whether it should run synchronously or asynchronously, and any delay before the next task is executed.
     *
     * @param task  The Runnable task to execute. This may be null if the link is only meant to introduce a delay before the next task.
     * @param sync  A boolean indicating whether the task should be run synchronously on the main server thread (true) or asynchronously on a separate thread (false).
     * @param delay The number of ticks to wait before executing the next task in the chain after this one is completed. This allows for scheduling tasks with specific timing between them.
     */
    private record ChainLink(Runnable task, boolean sync, long delay) {}
}
