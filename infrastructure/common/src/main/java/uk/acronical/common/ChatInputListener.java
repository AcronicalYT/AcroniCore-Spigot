package uk.acronical.common;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {

    private record InputRequest(Plugin plugin, Consumer<String> callback) {}

    private static final Map<UUID, InputRequest> inputs = new ConcurrentHashMap<>();
    private static boolean registered = false;

    /**
     * Registers a chat input listener for a specific player.
     *
     * @param plugin The plugin calling this method.
     * @param playerUUID The UUID of the player to listen for.
     * @param onInput The callback to execute when input is received.
     */
    public static void registerInput(Plugin plugin, UUID playerUUID, Consumer<String> onInput) {
        if (!registered) {
            plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(), plugin);
            registered = true;
        }

        inputs.put(playerUUID, new InputRequest(plugin, onInput));
    }

    /**
     * Handles player chat events to capture input.
     *
     * @param event The chat event.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        InputRequest request = inputs.remove(playerUUID);
        if (request != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(request.plugin, () -> request.callback.accept(event.getMessage()));
        }
    }

}
