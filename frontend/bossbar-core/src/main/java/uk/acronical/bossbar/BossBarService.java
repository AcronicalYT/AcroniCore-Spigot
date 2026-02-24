package uk.acronical.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A service for managing "Global" boss bars that should be visible to all online players.
 * <p>
 * This service automatically handles the addition and removal of players from
 * registered bars during join and quit events, utilising a thread-safe registry.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class BossBarService implements Listener {

    private final List<BaseBossBar> globalBars = new CopyOnWriteArrayList<>();

    /**
     * Registers a boss bar to be displayed to all current and future online players.
     *
     * @param bossBar The {@link BaseBossBar} to register as global.
     */
    public void registerGlobalBar(@NotNull BaseBossBar bossBar) {
        globalBars.add(bossBar);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
    }

    /**
     * Unregisters a global boss bar and removes it from all players' view.
     * <p>
     * This should be called when an event ends to ensure the bar is properly
     * disposed of and references are cleared.
     *
     * @param bossBar The {@link BaseBossBar} to unregister.
     */
    public void unregisterGlobalBar(@NotNull BaseBossBar bossBar) {
        globalBars.remove(bossBar);
        bossBar.removeAll();
    }

    /**
     * Automatically attaches all global boss bars to a joining player.
     *
     * @param event The player join event.
     */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        globalBars.forEach(bar -> bar.addPlayer(event.getPlayer()));
    }

    /**
     * Automatically detaches all global boss bars when a player quits.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        globalBars.forEach(bar -> bar.removePlayer(event.getPlayer()));
    }
}
