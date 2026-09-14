package uk.acronical.quest;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a defined quest template containing a series of objectives.
 * <p>
 * This class acts as the immutable blueprint for a quest, defining the requirements
 * and the reward executed upon completion. Player progression should be tracked
 * separately to ensure this template remains stateless and thread-safe.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class Quest {

    private final String id;
    private final List<Objective> objectives = new ArrayList<>();
    private Consumer<Player> onComplete;

    /**
     * Initialises a new quest template.
     *
     * @param id The unique identifier for this quest.
     */
    public Quest(@NotNull String id) {
        this.id = id;
    }

    /**
     * Retrieves the unique identifier for this quest.
     *
     * @return The quest ID.
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Appends a new objective requirement to this quest.
     *
     * @param objective The objective to add.
     * @return This quest instance for chaining.
     */
    @NotNull
    public Quest addObjective(@NotNull Objective objective) {
        this.objectives.add(objective);
        return this;
    }

    /**
     * Retrieves an unmodifiable view of the quest's objectives.
     *
     * @return The list of registered objectives.
     */
    @NotNull
    public List<Objective> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    /**
     * Defines the action to execute when a player successfully fulfils
     * all objectives within this quest.
     *
     * @param onComplete The reward logic consumer.
     * @return This quest instance for chaining.
     */
    @NotNull
    public Quest setReward(@Nullable Consumer<Player> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    /**
     * Triggers the completion logic and distributes rewards to a specific player.
     * <p>
     * Note: This method assumes the framework has already verified the player's
     * objective progression prior to execution.
     *
     * @param player The player completing the quest.
     */
    public void complete(@NotNull Player player) {
        if (onComplete != null) onComplete.accept(player);
    }
}
