package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BooleanSupplier;

public class ConditionalTask extends BukkitRunnable {

    private final Runnable task;
    private final BooleanSupplier condition;
    private final Runnable onStop;

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

    public void start(Plugin plugin, Runnable action, BooleanSupplier loopCondition, long period) {
        new ConditionalTask(action, loopCondition, null).runTaskTimer(plugin, 0, period);
    }
}
