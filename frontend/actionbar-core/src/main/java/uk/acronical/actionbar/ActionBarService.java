package uk.acronical.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
 * A service for dispatching and managing persistent action bar messages.
 * <p>
 * This utility facilitates both one-time messages and "sticky" persistent bars
 * that update automatically via a provided {@link Function}.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class ActionBarService implements Listener {

    private final Plugin plugin;
    private final TaskManager taskManager;

    private final Map<UUID, Function<Player, String>> persistentBars = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link ActionBarService} and starts the background refresh task.
     *
     * @param plugin      The plugin instance.
     * @param taskManager The {@link TaskManager} utilised for the update loop.
     */
    public ActionBarService(@NotNull Plugin plugin, @NotNull TaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        this.taskManager.async(() -> {
            for (Map.Entry<UUID, Function<Player, String>> entry : persistentBars.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    String currentText = entry.getValue().apply(player);
                    if (currentText != null && !currentText.isEmpty()) send(player, currentText);
                }
            }
        }, 20L, 20L);
    }

    /**
     * Sends a one-time action bar message to a player.
     *
     * @param player  The recipient player.
     * @param message The text to display (supports colour codes).
     */
    public void send(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(StringUtils.colour(message)));
    }

    /**
     * Sets a persistent action bar for a player that updates periodically.
     *
     * @param player   The player to receive the persistent bar.
     * @param provider A function that generates the current bar text for the player.
     */
    public void setPersistent(@NotNull Player player, @NotNull Function<Player, String> provider) {
        persistentBars.put(player.getUniqueId(), provider);
    }

    /**
     * Removes a persistent action bar and clears the current display for the player.
     *
     * @param player The player whose bar should be cleared.
     */
    public void clearPersistent(@NotNull Player player) {
        persistentBars.remove(player.getUniqueId());
        send(player, "");
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        persistentBars.remove(event.getPlayer().getUniqueId());
    }
}
