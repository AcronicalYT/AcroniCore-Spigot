package uk.acronical.quest;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The core service for managing and progressing player quests.
 * <p>
 * This service links stateless {@link Quest} templates with stateful
 * {@link QuestProgress} trackers. It evaluates incoming player actions
 * and automatically distributes rewards upon objective completion.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class QuestService {

    private final Map<String, Quest> registeredQuests = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, QuestProgress>> activePlayerQuests = new ConcurrentHashMap<>();

    /**
     * Registers a new quest blueprint into the framework.
     *
     * @param quest The quest template to register.
     */
    public void registerQuest(@NotNull Quest quest) {
        registeredQuests.put(quest.getId(), quest);
    }

    /**
     * Initiates a quest for a specific player, generating a new progression tracker.
     *
     * @param player  The target player.
     * @param questId The identifier of the quest to start.
     */
    public void startQuest(@NotNull Player player, @NotNull String questId) {
        Quest quest = registeredQuests.get(questId);
        if (quest == null) return;

        activePlayerQuests.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>()).putIfAbsent(questId, new QuestProgress(questId));
    }

    /**
     * Processes a specific action performed by a player, updating any active
     * quests that require this action.
     *
     * @param player   The player performing the action.
     * @param actionId The action identifier (e.g., "mine_iron_ore").
     * @param amount   The amount to increment the objective by.
     */
    public void progressAction(@NotNull Player player, @NotNull String actionId, int amount) {
        Map<String, QuestProgress> playerQuests = activePlayerQuests.get(player.getUniqueId());
        if (playerQuests == null || playerQuests.isEmpty()) return;

        for (QuestProgress progress : playerQuests.values()) {
            if (progress.isCompleted()) continue;

            Quest quest = registeredQuests.get(progress.getQuestId());
            if (quest == null) continue;

            boolean questUpdated = false;

            for (Objective objective : quest.getObjectives()) {
                if (objective.getActionId().equalsIgnoreCase(actionId)) {
                    int current = progress.getProgress(objective.getId());

                    if (current < objective.getTargetAmount()) {
                        progress.addProgress(objective.getId(), amount);
                        questUpdated = true;
                    }
                }
            }

            if (questUpdated) checkCompletion(player, quest, progress);
        }
    }

    /**
     * Evaluates if a player has met all requirements for a specific quest.
     */
    private void checkCompletion(@NotNull Player player, @NotNull Quest quest, @NotNull QuestProgress progress) {
        for (Objective obj : quest.getObjectives()) if (progress.getProgress(obj.getId()) < obj.getTargetAmount()) return;

        progress.markCompleted();
        quest.complete(player);
    }
}
