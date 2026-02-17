package uk.acronical.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for the Bukkit {@link Scoreboard} API to manage flicker-free sidebars.
 * <p>
 * This class utilises a team-based approach for updating lines, which prevents
 * the "flicker" effect commonly seen when resetting scores frequently.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class Sidebar {

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> currentLines = new ArrayList<>();

    private static final String[] lineIDs = new String[15];

    static {
        String codes = "0123456789abcdef";
        for (int i = 0; i < 15; i++) {
            lineIDs[i] = ChatColor.COLOR_CHAR + "" + codes.charAt(i) + ChatColor.RESET;
        }
    }

    /**
     * Initialises a new {@link Sidebar} for the specified player.
     *
     * @param player The player who will view this sidebar.
     * @throws IllegalStateException If the server's {@link org.bukkit.scoreboard.ScoreboardManager} is unavailable.
     */
    public Sidebar(@NotNull Player player) {
        this.player = player;
        if (Bukkit.getScoreboardManager() == null) throw new IllegalStateException("ScoreboardManager is not available");
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, "Title");
        this.objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    /**
     * Updates the title displayed at the top of the sidebar.
     *
     * @param title The new title string.
     */
    public void setTitle(@NotNull String title) {
        if (!objective.getDisplayName().equals(title)) objective.setDisplayName(title);
    }

    /**
     * Refreshes the lines displayed on the sidebar.
     * <p>
     * This method maps the provided strings to team prefixes associated with unique
     * colour codes. This allows for duplicate lines and dynamic updates without
     * visual artifacts.
     *
     * @param lines A list of strings to display, limited to 15 lines.
     * @throws IllegalArgumentException If the provided list exceeds 15 lines.
     */
    public void updateLines(List<String> lines) {
        if (lines.size() > 15) throw new IllegalArgumentException("Cannot have more than 15 lines in the sidebar");

        for (int i = 0; i < lines.size(); i++) {
            String text = lines.get(i);
            String lineID = lineIDs[i];

            Team team = scoreboard.getTeam(lineID);
            if (team == null) {
                team = scoreboard.registerNewTeam(lineID);
                team.addEntry(lineID);
            }

            if (!team.getPrefix().equals(text)) team.setPrefix(text);

            objective.getScore(lineID).setScore(lines.size() - i);
        }

        for (int i = lines.size(); i < 15; i++) {
            String lineID = lineIDs[i];
            scoreboard.resetScores(lineID);
            Team team = scoreboard.getTeam(lineID);
            if (team != null) team.unregister();
        }

        this.currentLines.clear();
        this.currentLines.addAll(lines);
    }

    /**
     * Retrieves the player associated with this sidebar.
     *
     * @return The {@link Player} instance.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }
}
