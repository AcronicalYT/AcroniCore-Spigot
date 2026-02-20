package uk.acronical.ui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.ui.input.SignInput;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service responsible for managing custom player input via sign interfaces.
 * <p>
 * This service utilises virtual block changes to present a sign editor to the player
 * without modifying the actual game world, capturing the results via {@link SignInput}.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class InputService implements Listener {

    private final Plugin plugin;
    private final Map<UUID, SignInput> activeSignInputs = new ConcurrentHashMap<>();
    private final Map<UUID, Location> fakeSignLocations = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link InputService}.
     *
     * @param plugin The plugin instance registering the service.
     */
    public InputService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens a virtual sign editor for the targeted player.
     * <p>
     * The sign is "placed" at the minimum world height to ensure it remains
     * out of view while the player interacts with the GUI.
     *
     * @param input The {@link SignInput} configuration and callback.
     */
    public void openSignInput(@NotNull SignInput input) {
        Player player = input.getTarget();
        UUID playerId = player.getUniqueId();

        Location location = player.getLocation().clone();
        location.setY(player.getWorld().getMinHeight() + 1);

        player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
        player.sendSignChange(location, input.getDefaultLines());

        Sign sign = (Sign) location.getBlock().getState();
        player.openSign(sign);

        activeSignInputs.put(playerId, input);
        fakeSignLocations.put(playerId, location);
    }

    /**
     * Captures the input once the player finishes editing the sign.
     *
     * @param event The sign change event.
     */
    @EventHandler
    public void onSignChange(@NotNull SignChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!activeSignInputs.containsKey(playerId)) return;

        Location expectedLocation = fakeSignLocations.get(playerId);
        if (expectedLocation == null || !event.getBlock().getLocation().equals(expectedLocation)) return;

        SignInput input = activeSignInputs.remove(playerId);
        fakeSignLocations.remove(playerId);

        player.sendBlockChange(expectedLocation, expectedLocation.getBlock().getBlockData());

        if (input != null) input.complete(event.getLines());
    }

    /**
     * Ensures references are cleared if a player disconnects during input.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        activeSignInputs.remove(uuid);
        fakeSignLocations.remove(uuid);
    }
}
