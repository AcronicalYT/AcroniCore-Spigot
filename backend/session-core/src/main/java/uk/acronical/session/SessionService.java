package uk.acronical.session;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.LoggerUtils;
import uk.acronical.common.StringUtils;
import uk.acronical.task.TaskManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A generic service for managing player session lifecycles.
 * <p>
 * This service handles asynchronous loading during the login phase and
 * automated background saving upon disconnection, utilising the provided
 * {@link SessionProvider} for persistence.
 *
 * @param <T> The type of session object managed.
 * @author Acronical
 * @since 1.0.3
 */
public class SessionService<T> implements Listener {

    private final Plugin plugin;
    private final TaskManager taskManager;
    private final SessionProvider<T> sessionProvider;

    private final Map<UUID, T> activeSessions = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link SessionService}.
     *
     * @param plugin          The plugin instance.
     * @param taskManager     The {@link TaskManager} for asynchronous operations.
     * @param sessionProvider The implementation used to load and save data.
     */
    public SessionService(@NotNull Plugin plugin, @NotNull TaskManager taskManager, @NotNull SessionProvider<T> sessionProvider) {
        this.plugin = plugin;
        this.taskManager = taskManager;
        this.sessionProvider = sessionProvider;
    }

    /**
     * Retrieves the active session for a player.
     *
     * @param player The player to query.
     * @return The session object, or {@code null} if no session is active.
     */
    @Nullable
    public T get(@NotNull Player player) {
        return activeSessions.get(player.getUniqueId());
    }

    /**
     * Retrieves the active session for a specific {@link UUID}.
     *
     * @param uuid The unique ID to query.
     * @return The session object, or {@code null}.
     */
    @Nullable
    public T get(@NotNull UUID uuid) {
        return activeSessions.get(uuid);
    }

    /**
     * Handles asynchronous data loading during the pre-login phase.
     * <p>
     * If the provider fails to load the data, the player's connection is
     * terminated to prevent inconsistent state or data loss.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID uuid = event.getUniqueId();

        try {
            T session = sessionProvider.load(uuid);
            activeSessions.put(uuid, session);
        } catch (Exception e) {
            LoggerUtils.severe("Failed to load session for " + event.getName() + "!");
            LoggerUtils.severe(e.getMessage());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, StringUtils.colour("§cFailed to load your player data. Please contact an administrator."));
        }
    }

    /**
     * Triggers an asynchronous save and removes the session upon player disconnection.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        T session = activeSessions.remove(uuid);

        if (session != null) {
            taskManager.async(() -> {
                try {
                    sessionProvider.save(uuid, session);
                } catch (Exception e) {
                    LoggerUtils.severe("Failed to save session for " + uuid + "!");
                    LoggerUtils.severe(e.getMessage());
                }
            });
        }
    }

    /**
     * Synchronously persists all active sessions to storage.
     * <p>
     * This method should typically be called during {@code onDisable} to
     * ensure no data is lost during a server shutdown or reload.
     */
    public void saveAllSync() {
        LoggerUtils.info("Saving all active player sessions...");

        for (Map.Entry<UUID, T> entry : activeSessions.entrySet()) {
            try {
                sessionProvider.save(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                LoggerUtils.severe("Failed to save session for " + entry.getKey());
                LoggerUtils.severe(e.getMessage());
            }
        }
    }
}
