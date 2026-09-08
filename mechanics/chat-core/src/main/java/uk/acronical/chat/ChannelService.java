package uk.acronical.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the routing and formatting of player chat across distinct channels.
 * <p>
 * This service intercepts asynchronous chat events and redirects them to the
 * appropriate channel (e.g., Staff, Party, Global), handling permissions
 * and specific recipient lists automatically.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class ChannelService implements Listener {

    private final Map<String, ChatChannel> registeredChannels = new ConcurrentHashMap<>();
    private final Map<UUID, ChatChannel> activeChannels = new ConcurrentHashMap<>();
    private ChatChannel defaultChannel;

    /**
     * Registers a new chat channel into the framework.
     *
     * @param channel The channel to register.
     */
    public void registerChannel(@NotNull ChatChannel channel) {
        registeredChannels.putIfAbsent(channel.getIdentifier().toLowerCase(), channel);
    }

    /**
     * Designates a channel as the default fallback for all players.
     *
     * @param channel The default channel.
     */
    public void setDefaultChannel(@NotNull ChatChannel channel) {
        this.defaultChannel = channel;
        registerChannel(channel);
    }

    /**
     * Updates the active speaking channel for a specific player.
     *
     * @param player     The player changing channels.
     * @param identifier The identifier of the target channel.
     */
    public void setActiveChannel(@NotNull Player player, @NotNull String identifier) {
        ChatChannel channel = registeredChannels.get(identifier.toLowerCase());
        if (channel == null) channel = defaultChannel;
        if (channel == null) activeChannels.remove(player.getUniqueId());
        else activeChannels.put(player.getUniqueId(), channel);
    }

    /**
     * Retrieves the channel a player is currently speaking in.
     *
     * @param player The player to check.
     * @return The active channel, or the default channel if none is set.
     */
    public ChatChannel getActiveChannel(@NotNull Player player) {
        return activeChannels.getOrDefault(player.getUniqueId(), defaultChannel);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        ChatChannel targetChannel = getActiveChannel(sender);

        if (targetChannel == null) {
            event.setCancelled(true);
            sender.sendMessage("§cYou are not in an active chat channel.");
            return;
        }

        if (!targetChannel.canSpeak(sender)) {
            event.setCancelled(true);
            sender.sendMessage("§cYou do not have permission to speak in this channel.");
            return;
        }

        String formattedMessage = targetChannel.formatMessage(sender, event.getMessage());
        event.setFormat(formattedMessage.replace("%", "%%"));

        Set<Player> recipients = targetChannel.getRecipients(sender);
        event.getRecipients().clear();
        event.getRecipients().addAll(recipients);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activeChannels.remove(event.getPlayer().getUniqueId());
    }
}
