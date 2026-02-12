package uk.acronical.task;

import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class CountdownBuilder {

    private final Plugin plugin;
    private int seconds = 10;
    private Consumer<Integer> onTick;
    private Runnable onFinish;
    private Runnable onCancel;

    public CountdownBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the number of seconds for the countdown.
     *
     * @param seconds The number of seconds for the countdown
     * @return The CountdownBuilder instance, allowing for method chaining
     */
    public CountdownBuilder from(int seconds) {
        this.seconds = seconds;
        return this;
    }

    /**
     * Sets the action to perform on each tick of the countdown, receiving the remaining seconds as an argument.
     *
     * @param onTick The action to perform on each tick of the countdown
     * @return The CountdownBuilder instance, allowing for method chaining
     */
    public CountdownBuilder onTick(Consumer<Integer> onTick) {
        this.onTick = onTick;
        return this;
    }

    /**
     * Sets the action to perform when the countdown finishes.
     *
     * @param onFinish The action to perform when the countdown finishes
     * @return The CountdownBuilder instance, allowing for method chaining
     */
    public CountdownBuilder onFinish(Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Sets the action to perform if the countdown is cancelled.
     *
     * @param onCancel The action to perform if the countdown is cancelled
     * @return The CountdownBuilder instance, allowing for method chaining
     */
    public CountdownBuilder onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * Starts the countdown task with the configured settings.
     *
     * @return The CountdownTask that was started
     */
    public CountdownTask start() {
        CountdownTask task = new CountdownTask(seconds, onTick, onFinish, onCancel);
        task.runTaskTimer(plugin, 0L, 20L);
        return task;
    }
}
