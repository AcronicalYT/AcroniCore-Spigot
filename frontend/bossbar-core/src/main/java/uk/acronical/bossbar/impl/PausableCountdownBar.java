package uk.acronical.bossbar.impl;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A countdown boss bar that can be paused, resumed, and toggled visually.
 * <p>
 * This class extends the base countdown functionality by allowing the tick
 * process to be halted without cancelling the underlying task.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class PausableCountdownBar extends CountdownBar {

    private final TaskManager taskManager;
    private int totalTicks;
    private int remainingTicks;
    private int taskId = -1;

    private Consumer<List<Player>> onComplete;
    private boolean paused = false;

    /**
     * Initialises a new pausable countdown bar.
     *
     * @param taskManager The {@link TaskManager} to handle the repeating sync task.
     * @param seconds     The seconds to run the countdown for.
     * @param title       The display title.
     * @param barColor    The bar colour.
     * @param barStyle    The bar style.
     */
    public PausableCountdownBar(@NotNull TaskManager taskManager, int seconds, boolean autoStart, @NotNull String title, @NotNull BarColor barColor, @NotNull BarStyle barStyle) {
        super(taskManager, seconds, autoStart, title, barColor, barStyle);
        this.taskManager = taskManager;
        this.bossBar.setProgress(1.0);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public void start(int seconds) {
        if (taskId != -1) taskManager.cancel(taskId);

        this.totalTicks = seconds * 20;
        this.remainingTicks = totalTicks;

        setIsVisible(true);
        this.taskId = taskManager.sync(() -> {
            if (paused) return;

            remainingTicks--;

            if (remainingTicks <= 0) {
                finish();
                setIsVisible(false);
                return;
            }

            double progress = (double) remainingTicks / totalTicks;
            bossBar.setProgress(Math.clamp(progress, 0.0, 1.0));
        }, 0L, 1L).getTaskId();
    }

    @Override
    public void finish() {
        if (taskId != -1) {
            taskManager.cancel(taskId);
            taskId = -1;
        }

        List<Player> snapshot = new ArrayList<>(viewers);
        removeAll();

        if (onComplete != null) {
            onComplete.accept(snapshot);
        }
    }
}
