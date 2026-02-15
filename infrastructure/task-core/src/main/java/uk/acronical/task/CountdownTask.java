package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class CountdownTask extends BukkitRunnable {

    private int current;
    private final Consumer<Integer> tick;
    private final Runnable finish;
    private final Runnable cancel;

    /**
     * Constructs a new CountdownTask with the specified starting count, tick action, finish action, and cancel action.
     *
     * @param start  the starting count for the countdown
     * @param tick   the action to execute on each tick of the countdown, accepting the current count as an argument
     * @param finish the action to execute when the countdown reaches zero
     * @param cancel the action to execute if the countdown is cancelled before reaching zero
     */
    public CountdownTask(int start, Consumer<Integer> tick, Runnable finish, Runnable cancel) {
        this.current = start;
        this.tick = tick;
        this.finish = finish;
        this.cancel = cancel;
    }

    /**
     * Runs the countdown task, decrementing the current count each time it is called. If the current count reaches zero, the finish action is executed and the task is cancelled. If the task is cancelled before reaching zero, the cancel action is executed.
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
     * Cancels the countdown task, executing the cancel action if the current count is greater than zero.
     */
    @Override
    public void cancel() {
        super.cancel();
        if (this.current > 0 && cancel != null) {
            cancel.run();
        }
    }
}