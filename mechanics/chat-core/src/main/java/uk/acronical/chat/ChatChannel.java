package uk.acronical.chat;

import org.bukkit.entity.Player;

import java.util.Set;

public interface ChatChannel {
    /**
     * The unique identifier for this channel (e.g., "global", "staff", "party").
     */
    String getIdentifier();

    /**
     * Determines the visual format of the message.
     * Supports legacy colour codes or modern formatting.
     */
    String formatMessage(Player sender, String message);

    /**
     * Calculates who should receive the message.
     * For local chat, this would check distances. For party chat, it queries party-core.
     */
    Set<Player> getRecipients(Player sender);

    /**
     * Determines if a player has permission to speak in this channel.
     */
    boolean canSpeak(Player player);
}
