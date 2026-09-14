package uk.acronical.quest;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks a specific player's ongoing progression through a quest.
 * <p>
 * This model handles the mutable state of quest objectives using thread-safe
 * operations, ensuring accurate tracking even during highly concurrent
 * asynchronous events (e.g., area-of-effect damage or rapid block breaking).
 *
 * @author Acronical
 * @since 1.0.6
 */
public class QuestProgress {

    private final String questId;
    private final Map<String, Integer> objectiveProgress = new ConcurrentHashMap<>();
    private boolean completed = false;

    /**
     * Initialises a new quest progression tracker.
     *
     * @param questId The unique identifier of the quest being tracked.
     */
    public QuestProgress(@NotNull String questId) {
        this.questId = questId;
    }

    /**
     * Retrieves the ID of the quest this progress object is tracking.
     *
     * @return The quest ID.
     */
    @NotNull
    public String getQuestId() {
        return questId;
    }

    /**
     * Checks if the player has fully completed this quest.
     *
     * @return True if completed, false otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Retrieves the current progress for a specific objective.
     *
     * @param objectiveId The unique identifier of the objective.
     * @return The current amount completed, or 0 if no progress has been made.
     */
    public int getProgress(@NotNull String objectiveId) {
        return objectiveProgress.getOrDefault(objectiveId, 0);
    }

    /**
     * Safely increments the progress for a specific objective.
     *
     * @param objectiveId The unique identifier of the objective.
     * @param amount      The amount to add (must be positive).
     */
    public void addProgress(@NotNull String objectiveId, int amount) {
        if (completed) return;
        if (amount <= 0) throw new IllegalArgumentException("Progress amount must be greater than zero. Provided: " + amount);
        objectiveProgress.merge(objectiveId, amount, Integer::sum);
    }

    /**
     * Marks this quest progression as fully completed.
     */
    public void markCompleted() {
        this.completed = true;
    }

    /**
     * Loads existing progress data from the serialisation core.
     *
     * @param loadedProgress The deserialised map of objective progress.
     */
    public void loadProgress(@NotNull Map<String, Integer> loadedProgress) {
        this.objectiveProgress.clear();
        loadedProgress.forEach((key, value) -> this.objectiveProgress.put(key.toLowerCase(), value));
    }

    /**
     * Retrieves an unmodifiable view of the raw progress for saving.
     *
     * @return A read-only map of all active objective progress.
     */
    @NotNull
    public Map<String, Integer> getRawProgress() {
        return Collections.unmodifiableMap(objectiveProgress);
    }
}
