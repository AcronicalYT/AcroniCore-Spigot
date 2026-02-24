package uk.acronical.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A foundational wrapper for the Bukkit {@link BossBar} system.
 * <p>
 * This class facilitates the management of boss bars, providing a base for
 * custom implementations such as automated timers or progress trackers.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class BaseBossBar {

    protected final BossBar bossBar;
    protected final List<Player> viewers = new ArrayList<>();

    /**
     * Initialises a new boss bar with the specified properties.
     *
     * @param title    The text displayed at the top of the bar.
     * @param barColor The {@link BarColor} (e.g., RED, BLUE, GREEN).
     * @param barStyle The {@link BarStyle} (e.g., SOLID, SEGMENTED_10).
     */
    public BaseBossBar(@NotNull String title, @NotNull BarColor barColor, @NotNull BarStyle barStyle) {
        this.bossBar = Bukkit.createBossBar(title, barColor, barStyle);
    }

    /**
     * Attaches a player to this boss bar, making it visible to them.
     *
     * @param player The player to add.
     * @return The current {@link BaseBossBar} instance.
     */
    public BaseBossBar addPlayer(@NotNull Player player) {
        bossBar.addPlayer(player);
        if (!viewers.contains(player)) viewers.add(player);
        return this;
    }

    /**
     * Detaches a player from this boss bar, hiding it from their view.
     *
     * @param player The player to remove.
     * @return The current {@link BaseBossBar} instance.
     */
    public BaseBossBar removePlayer(@NotNull Player player) {
        bossBar.removePlayer(player);
        viewers.remove(player);
        return this;
    }

    /**
     * Finalises the boss bar by removing all current viewers.
     */
    public void removeAll() {
        bossBar.removeAll();
        viewers.clear();
    }

    /**
     * An entrypoint to run custom logic when the bossbar is shown.
     */
    public void onRegister() { }

    /**
     * An entrypoint to run custom logic when the bossbar is removed.
     */
    public void onUnregister() {
        removeAll();
    }

    /**
     * Retrieves the underlying native Bukkit {@link BossBar}.
     *
     * @return The native boss bar instance.
     */
    @NotNull
    public BossBar getNativeBar() {
        return bossBar;
    }
}
