package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * A specialised {@link BukkitRunnable} that executes logic based on a dynamic condition.
 * <p>
 * This task will repeat until the provided {@link BooleanSupplier} returns {@code false},
 * at which point it will optionally execute a termination task and cancel itself.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class ConditionalTask extends BukkitRunnable {

    private final Runnable task;
    private final BooleanSupplier condition;
    private final Runnable onStop;

    /**
     * Initialises a new {@link ConditionalTask}.
     *
     * @param task      The logic to execute while the condition remains {@code true}.
     * @param condition The requirement check performed before each execution.
     * @param onStop    An optional action to execute when the task is cancelled (may be {@code null}).
     */
    public ConditionalTask(@NotNull Runnable task, @NotNull BooleanSupplier condition, @Nullable Runnable onStop) {
        this.task = task;
        this.condition = condition;
        this.onStop = onStop;
    }

    /**
     * Executes the task logic.
     * <p>
     * If the {@link BooleanSupplier} returns {@code false}, the {@code onStop} logic
     * is triggered and the task is cancelled. Otherwise, the primary task runs.
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
     * Creates and starts a new {@link ConditionalTask} timer.
     * <p>
     * This serves as a factory method to quickly launch a repeating task with
     * no initial delay and no {@code onStop} action.
     *
     * @param plugin        The {@link Plugin} instance responsible for the task.
     * @param action        The logic to execute.
     * @param loopCondition The condition to check before each run.
     * @param period        The time to wait between runs, measured in server ticks.
     */
    public void start(@NotNull Plugin plugin, @NotNull Runnable action, @NotNull BooleanSupplier loopCondition, long period) {
        new ConditionalTask(action, loopCondition, null).runTaskTimer(plugin, 0, period);
    }
}
