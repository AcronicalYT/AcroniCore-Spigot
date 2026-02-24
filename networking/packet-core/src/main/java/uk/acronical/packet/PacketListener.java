package uk.acronical.packet;

import org.jetbrains.annotations.NotNull;

/**
 * An interface for listening to and manipulating raw network packets.
 * <p>
 * Implementations of this interface can be registered with the packet service
 * to intercept traffic. This allows for low-level modifications of the
 * Minecraft protocol before data is finalised.
 *
 * @author Acronical
 * @since 1.0.3
 */
public interface PacketListener {

    /**
     * Called when a packet is about to be sent from the server to a player (Outgoing).
     * <p>
     * This is the ideal stage to modify data before the client receives it, such as
     * hiding entities or altering metadata.
     *
     * @param event The {@link PacketEvent} containing the packet and recipient.
     */
    default void onPacketSend(@NotNull PacketEvent event) {}

    /**
     * Called when a packet is received by the server from a player (Incoming).
     * <p>
     * Utilise this to intercept player actions, such as movement, interaction,
     * or custom payload data, before the server processes them.
     *
     * @param event The {@link PacketEvent} containing the packet and sender.
     */
    default void onPacketReceive(@NotNull PacketEvent event) {}
}
