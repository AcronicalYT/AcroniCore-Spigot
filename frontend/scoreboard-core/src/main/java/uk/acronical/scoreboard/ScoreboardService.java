package uk.acronical.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.task.TaskManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A service for managing and synchronising dynamic sidebars for online players.
 * <p>
 * This service utilises a provider-based system to supply titles and lines
 * on a per-player basis, refreshed via a repeating background task.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class ScoreboardService {

    private final Map<UUID, Sidebar> boards = new ConcurrentHashMap<>();
    private Function<Player, String> titleProvider;
    private Function<Player, List<String>> lineProvider;

    /**
     * Initialises the {@link ScoreboardService} and starts the refresh task.
     *
     * @param plugin      The plugin instance for server access.
     * @param taskManager The {@link TaskManager} used to schedule updates.
     */
    public ScoreboardService(@NotNull Plugin plugin, @NotNull TaskManager taskManager) {
        taskManager.async(() -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                update(player);
            }
        }, 20L, 20L);
    }

    /**
     * Sets the provider responsible for generating the sidebar title.
     *
     * @param titleProvider A function that returns a title for a given {@link Player}.
     */
    public void setTitleProvider(@NotNull Function<Player, String> titleProvider) {
        this.titleProvider = titleProvider;
    }

    /**
     * Sets the provider responsible for generating the sidebar lines.
     *
     * @param lineProvider A function that returns a list of lines for a given {@link Player}.
     */
    public void setLineProvider(@NotNull Function<Player, List<String>> lineProvider) {
        this.lineProvider = lineProvider;
    }

    /**
     * Updates the {@link Sidebar} for a specific player.
     *
     * @param player The player whose sidebar should be refreshed.
     */
    private void update(@NotNull Player player) {
        Sidebar sidebar = boards.computeIfAbsent(player.getUniqueId(), uuid -> new Sidebar(player));

        if (titleProvider != null) sidebar.setTitle(titleProvider.apply(player));
        if (lineProvider != null) sidebar.updateLines(lineProvider.apply(player));
    }

    /**
     * Removes and cleans up the stored {@link Sidebar} for a player.
     *
     * @param player The player who is leaving or no longer requires a sidebar.
     */
    public void remove(@NotNull Player player) {
        boards.remove(player.getUniqueId());
    }
}
