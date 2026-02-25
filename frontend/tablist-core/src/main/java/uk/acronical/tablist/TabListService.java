package uk.acronical.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.StringUtils;
import uk.acronical.task.TaskManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A service for managing dynamic player list (TabList) headers and footers.
 * <p>
 * This utility facilitates the use of persistent, auto-refreshing text at the
 * top and bottom of the tab menu, utilised to display server information
 * or player-specific statistics.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class TabListService implements Listener {

    private final Plugin plugin;
    private final TaskManager taskManager;

    private final Map<UUID, Function<Player, String>> headers = new ConcurrentHashMap<>();
    private final Map<UUID, Function<Player, String>> footers = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link TabListService} and starts the background update loop.
     *
     * @param plugin      The plugin instance.
     * @param taskManager The {@link TaskManager} utilised for periodic synchronisation.
     */
    public TabListService(@NotNull Plugin plugin, @NotNull TaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        this.taskManager.async(() -> {
           for (Player player : Bukkit.getOnlinePlayers()) updateTabList(player);
        }, 20L, 20L);
    }

    /**
     * Sets a dynamic header provider for a specific player.
     *
     * @param player         The recipient player.
     * @param headerProvider A function generating the header text (supports colour codes).
     */
    public void setHeader(@NotNull Player player, @NotNull Function<Player, String> headerProvider) {
        headers.put(player.getUniqueId(), headerProvider);
        updateTabList(player);
    }

    /**
     * Sets a dynamic footer provider for a specific player.
     *
     * @param player         The recipient player.
     * @param footerProvider A function generating the footer text.
     */
    public void setFooter(@NotNull Player player, @NotNull Function<Player, String> footerProvider) {
        footers.put(player.getUniqueId(), footerProvider);
        updateTabList(player);
    }

    /**
     * Removes all dynamic providers and clears the tab list display for a player.
     *
     * @param player The player to clear.
     */
    public void clear(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        headers.remove(uuid);
        footers.remove(uuid);
        player.setPlayerListHeaderFooter(null, null);
    }

    /**
     * Internal method to resolve provider functions and push updates to the client.
     */
    private void updateTabList(@NotNull Player player) {
        UUID uuid = player.getUniqueId();

        Function<Player, String> headerFunction = headers.get(uuid);
        Function<Player, String> footerFunction = footers.get(uuid);

        String header = headerFunction != null ? headerFunction.apply(player) : "";
        String footer = footerFunction != null ? footerFunction.apply(player) : "";

        player.setPlayerListHeaderFooter(StringUtils.colour(header), StringUtils.colour(footer));
    }

    /**
     * Ensures memory is freed by removing providers when a player disconnects.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        headers.remove(uuid);
        footers.remove(uuid);
    }
}
