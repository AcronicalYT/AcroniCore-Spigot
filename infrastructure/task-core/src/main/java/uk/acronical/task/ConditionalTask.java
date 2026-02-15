package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BooleanSupplier;

public class ConditionalTask extends BukkitRunnable {

    private final Runnable task;
    private final BooleanSupplier condition;
    private final Runnable onStop;

    /**
     * Creates a new ConditionalTask.
     *
     * @param task      The task to execute if the condition is true.
     * @param condition The condition to check before executing the task.
     * @param onStop    The action to execute if the condition is false and the task is cancelled (can be null).
     */
    public ConditionalTask(Runnable task, BooleanSupplier condition, Runnable onStop) {
        this.task = task;
        this.condition = condition;
        this.onStop = onStop;
    }

    /**
     * Runs the conditional task, executing the provided task if the condition is true. If the condition is false, the onStop action is executed and the task is cancelled.
     */
    @Override
    public void run() {
        if (!condition.getAsBoolean()) {
            if (onStop != null) onStop.run();
            this.cancel();
            return;
        }

        task.run();
    }

    /**
     * Starts a new ConditionalTask with the given parameters.
     *
     * @param plugin        The plugin to run the task for.
     * @param action        The task to execute if the condition is true.
     * @param loopCondition The condition to check before executing the task.
     * @param period        The period in ticks between each execution of the task.
     */
    public void start(Plugin plugin, Runnable action, BooleanSupplier loopCondition, long period) {
        new ConditionalTask(action, loopCondition, null).runTaskTimer(plugin, 0, period);
    }
}
