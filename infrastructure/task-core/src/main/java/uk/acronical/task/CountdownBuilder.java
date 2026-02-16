package uk.acronical.task;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A fluent builder for creating and starting {@link CountdownTask} instances.
 * <p>
 * This utility simplifies the creation of timed countdowns by allowing
 * method chaining for tick actions, termination logic, and cancellation hooks.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class CountdownBuilder {

    private final Plugin plugin;
    private int seconds = 10;
    private Consumer<Integer> onTick;
    private Runnable onFinish;
    private Runnable onCancel;

    /**
     * Initialises a new {@link CountdownBuilder} for the specified {@link Plugin}.
     *
     * @param plugin The plugin instance responsible for running the countdown.
     */
    public CountdownBuilder(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the starting duration of the countdown.
     *
     * @param seconds The number of seconds to count down from.
     * @return The current {@link CountdownBuilder} instance for method chaining.
     */
    public CountdownBuilder from(int seconds) {
        this.seconds = seconds;
        return this;
    }

    /**
     * Defines the action to perform on every second of the countdown.
     *
     * @param onTick A consumer receiving the remaining seconds as an integer.
     * @return The current {@link CountdownBuilder} instance for method chaining.
     */
    public CountdownBuilder onTick(@NotNull Consumer<Integer> onTick) {
        this.onTick = onTick;
        return this;
    }

    /**
     * Defines the action to perform when the countdown reaching zero.
     *
     * @param onFinish The logic to execute upon successful completion.
     * @return The current {@link CountdownBuilder} instance for method chaining.
     */
    public CountdownBuilder onFinish(@Nullable Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Defines the action to perform if the countdown is manually cancelled.
     *
     * @param onCancel The logic to execute upon cancellation.
     * @return The current {@link CountdownBuilder} instance for method chaining.
     */
    public CountdownBuilder onCancel(@Nullable Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * Finalises the configuration and starts the {@link CountdownTask}.
     * <p>
     * This method schedules the task to run every 20 server ticks (1 second).
     *
     * @return The started {@link CountdownTask} instance.
     */
    @NotNull
    public CountdownTask start() {
        CountdownTask task = new CountdownTask(seconds, onTick, onFinish, onCancel);
        task.runTaskTimer(plugin, 0L, 20L);
        return task;
    }
}
