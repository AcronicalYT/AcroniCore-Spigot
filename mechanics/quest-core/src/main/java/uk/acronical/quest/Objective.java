package uk.acronical.quest;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a discrete requirement within a quest.
 * <p>
 * This immutable model defines the specific action a player must perform
 * and the quantity required to fulfil this segment of the quest.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class Objective {

    private final String id;
    private final String actionId;
    private final int targetAmount;

    /**
     * Initialises a new quest objective.
     *
     * @param id           The unique identifier for this specific objective.
     * @param actionId     The identifier of the action required (e.g., "block_break", "entity_kill").
     * @param targetAmount The quantity required to complete the objective (must be greater than zero).
     * @throws IllegalArgumentException if the target amount is less than 1.
     */
    public Objective(@NotNull String id, @NotNull String actionId, int targetAmount) {
        if (targetAmount <= 0) throw new IllegalArgumentException("Target amount must be at least 1. Provided: " + targetAmount);
        this.id = id;
        this.actionId = actionId;
        this.targetAmount = targetAmount;
    }

    /**
     * Retrieves the unique identifier for this objective.
     *
     * @return The objective ID.
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Retrieves the trigger action identifier that progresses this objective.
     *
     * @return The action ID.
     */
    @NotNull
    public String getActionId() {
        return actionId;
    }

    /**
     * Retrieves the total number of actions required for completion.
     *
     * @return The target amount.
     */
    public int getTargetAmount() {
        return targetAmount;
    }
}
