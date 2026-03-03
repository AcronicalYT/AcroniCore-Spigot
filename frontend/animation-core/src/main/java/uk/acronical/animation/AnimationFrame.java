package uk.acronical.animation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a single step within a sequenced animation or task chain.
 * <p>
 * Each frame defines a specific action to be executed and the duration
 * to wait before progressing to the next frame in the sequence.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class AnimationFrame {

    private final Runnable action;
    private final long delayTicks;

    /**
     * Initialises a new animation frame.
     *
     * @param action     The logic to execute when this frame triggers.
     * @param delayTicks The delay in ticks (20 ticks = 1 second) before the next frame.
     */
    public AnimationFrame(@NotNull Runnable action, long delayTicks) {
        this.action = action;
        this.delayTicks = delayTicks;
    }

    /**
     * Retrieves the action associated with this frame.
     *
     * @return The {@link Runnable} logic.
     */
    @NotNull
    public Runnable getAction() {
        return action;
    }

    /**
     * Retrieves the delay duration for this frame.
     *
     * @return The delay in server ticks.
     */
    public long getDelayTicks() {
        return delayTicks;
    }
}
