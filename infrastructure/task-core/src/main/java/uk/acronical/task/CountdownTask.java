package uk.acronical.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A specialised {@link BukkitRunnable} that facilitates a numerical countdown.
 * <p>
 * This task decrements its internal counter on every run, triggering a tick
 * consumer until it reaches zero, at which point it executes the finish logic
 * and terminates.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class CountdownTask extends BukkitRunnable {

    private int current;
    private final Consumer<Integer> tick;
    private final Runnable finish;
    private final Runnable cancel;

    /**
     * Initialises a new {@link CountdownTask}.
     *
     * @param start  The starting value for the countdown.
     * @param tick   The logic to execute on each second, receiving the current count.
     * @param finish The logic to execute when the countdown naturally reaches zero.
     * @param cancel The logic to execute if the task is cancelled before completion.
     */
    public CountdownTask(int start, @Nullable Consumer<Integer> tick, @Nullable Runnable finish, @Nullable Runnable cancel) {
        this.current = start;
        this.tick = tick;
        this.finish = finish;
        this.cancel = cancel;
    }

    /**
     * Executes the countdown logic.
     * <p>
     * On each run, the {@code tick} consumer is notified of the current count.
     * Once the counter reaches zero, the {@code finish} action is executed and
     * the task is cancelled.
     */
    @Override
    public void run() {
        if (current <= 0) {
            if (finish != null) finish.run();
            this.cancel();
            return;
        }

        if (tick != null) tick.accept(current);
        current--;
    }

    /**
     * Prematurely cancels the countdown.
     * <p>
     * If the countdown has not yet reached zero, the {@code cancel} action
     * is triggered before the task is stopped.
     */
    @Override
    public void cancel() {
        super.cancel();
        if (this.current > 0 && cancel != null) {
            cancel.run();
        }
    }
}