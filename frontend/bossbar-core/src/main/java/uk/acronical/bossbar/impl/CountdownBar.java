package uk.acronical.bossbar.impl;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.bossbar.BaseBossBar;
import uk.acronical.task.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A specialised {@link BaseBossBar} that visualises a timed countdown.
 * <p>
 * This bar automatically decrements its progress over a specified duration,
 * utilised primarily for minigame starts, combat timers, or event durations.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class CountdownBar extends BaseBossBar {

    private final TaskManager taskManager;
    private int totalTicks;
    private int remainingTicks;
    private int taskId = -1;
    private Consumer<List<Player>> onComplete;

    /**
     * Initialises the countdown bar.
     *
     * @param taskManager The {@link TaskManager} to handle the repeating sync task.
     * @param title       The display title.
     * @param barColor    The bar colour.
     * @param barStyle    The bar style.
     */
    public CountdownBar(@NotNull TaskManager taskManager, @NotNull String title, @NotNull BarColor barColor, @NotNull BarStyle barStyle) {
        super(title, barColor, barStyle);
        this.taskManager = taskManager;
        this.bossBar.setProgress(1.0);
    }

    /**
     * Defines an action to be executed when the countdown reaches zero.
     *
     * @param action A {@link Consumer} receiving the list of players who were
     * viewing the bar at completion.
     * @return The current {@link CountdownBar} instance.
     */
    public CountdownBar onComplete(@Nullable Consumer<List<Player>> action) {
        this.onComplete = action;
        return this;
    }

    /**
     * Starts the countdown timer.
     * <p>
     * If a countdown is already in progress, it will be cancelled and restarted.
     *
     * @param seconds The total duration of the countdown in seconds.
     */
    public void start(int seconds) {
        if (taskId != -1) taskManager.cancel(taskId);

        this.totalTicks = seconds * 20;
        this.remainingTicks = totalTicks;

        this.taskId = taskManager.sync(() -> {
            remainingTicks--;

            if (remainingTicks <= 0) {
                finish();
                return;
            }

            double progress = (double) remainingTicks / totalTicks;
            bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

        }, 0L, 1L).getTaskId();
    }

    /**
     * Finalises the countdown, stops the task, and triggers the completion callback.
     */
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
