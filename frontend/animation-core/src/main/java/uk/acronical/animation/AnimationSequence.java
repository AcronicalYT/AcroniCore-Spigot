package uk.acronical.animation;

import org.jetbrains.annotations.NotNull;
import uk.acronical.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the scheduling of a linear sequence of actions over time.
 * <p>
 * This class utilise a cumulative delay strategy to schedule all frames
 * immediately upon the {@link #play()} call, ensuring a smooth and
 * predictable execution timeline.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class AnimationSequence {

    private final TaskManager taskManager;
    private final List<AnimationFrame> animationFrames = new ArrayList<>();

    private Runnable onStart, onEnd;

    /**
     * Initialises the animation sequence.
     *
     * @param taskManager The {@link TaskManager} used to dispatch the scheduled tasks.
     */
    public AnimationSequence(@NotNull TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * Defines an action to be executed immediately when the sequence starts.
     */
    public AnimationSequence onStart(@NotNull Runnable action) {
        this.onStart = action;
        return this;
    }

    /**
     * Appends a new frame to the end of the sequence.
     *
     * @param action     The logic to execute.
     * @param delayTicks The wait duration *after* the previous frame before this one runs.
     */
    public AnimationSequence appendFrame(@NotNull Runnable action, long delayTicks) {
        this.animationFrames.add(new AnimationFrame(action, delayTicks));
        return this;
    }

    /**
     * Defines an action to be executed after the final frame has completed.
     */
    public AnimationSequence onEnd(@NotNull Runnable action) {
        this.onEnd = action;
        return this;
    }

    /**
     * Schedules the entire sequence on the server's main thread.
     */
    public void play() {
        if (onStart != null) taskManager.sync(onStart);

        long absoluteTickDelay = 0;

        for (AnimationFrame animationFrame : animationFrames) {
            absoluteTickDelay += animationFrame.getDelayTicks();

            final Runnable action = animationFrame.getAction();

            if (absoluteTickDelay == 0) taskManager.sync(action);
            else taskManager.sync(action, absoluteTickDelay);
        }

        if (onEnd != null) {
            if (absoluteTickDelay == 0) taskManager.sync(onEnd);
            else taskManager.sync(onEnd, absoluteTickDelay);
        }
    }
}
