package uk.acronical.common;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * A utility class for capturing player chat as functional input.
 * <p>
 * This listener allows developers to "prompt" a player for text input via chat.
 * It intercepts the next message sent by the player, cancels the event to prevent
 * it from appearing in global chat, and executes a provided callback.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class ChatInputListener implements Listener {

    private record InputRequest(@NotNull Plugin plugin, @NotNull Consumer<String> callback) {}

    private static final Map<UUID, InputRequest> inputs = new ConcurrentHashMap<>();
    private static boolean registered = false;

    /**
     * Registers a chat input request for a specific player.
     * <p>
     * If this is the first time the utility is used, it will automatically
     * initialise and register itself as a {@link Listener}.
     *
     * @param plugin     The {@link Plugin} instance requesting the input.
     * @param playerUUID The {@link UUID} of the player to monitor.
     * @param onInput    The callback to execute once input is received.
     */
    public static void registerInput(@NotNull Plugin plugin, @NotNull UUID playerUUID, @NotNull Consumer<String> onInput) {
        if (!registered) {
            plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(), plugin);
            registered = true;
        }

        inputs.put(playerUUID, new InputRequest(plugin, onInput));
    }

    /**
     * Handles {@link AsyncPlayerChatEvent} to capture pending input requests.
     * <p>
     * This listener runs at {@link EventPriority#LOWEST} to ensure input is
     * captured before other chat-related plugins process the message. The
     * callback is executed on the main server thread.
     *
     * @param event The chat event being monitored.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        InputRequest request = inputs.remove(playerUUID);
        if (request != null) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask(request.plugin, () -> request.callback.accept(event.getMessage()));
        }
    }

}
