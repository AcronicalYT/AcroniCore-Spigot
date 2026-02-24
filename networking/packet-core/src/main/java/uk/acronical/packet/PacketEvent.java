package uk.acronical.packet;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a packet-level interaction intercepted between a player and the server.
 * <p>
 * This event facilitates the inspection and modification of raw Minecraft packets.
 * It can be utilised to implement custom client-side features or to cancel specific
 * network traffic entirely.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class PacketEvent {

    private final Player player;
    private Object packet;
    private boolean cancelled = false;

    /**
     * Initialises a new {@link PacketEvent}.
     *
     * @param player The player associated with this packet traffic.
     * @param packet The raw packet object (typically an NMS instance).
     */
    public PacketEvent(@NotNull Player player, @NotNull Object packet) {
        this.player = player;
        this.packet = packet;
    }

    /**
     * Retrieves the player whose connection generated or is receiving the packet.
     *
     * @return The {@link Player} instance.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the raw packet object being intercepted.
     *
     * @return The underlying packet instance.
     */
    @NotNull
    public Object getPacket() {
        return packet;
    }

    /**
     * Replaces the intercepted packet with a new packet instance.
     *
     * @param packet The replacement packet object.
     */
    public void setPacket(@NotNull Object packet) {
        this.packet = packet;
    }

    /**
     * Checks if the packet transmission has been cancelled.
     *
     * @return {@code true} if the packet will not be sent/received; otherwise {@code false}.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether the packet transmission should be cancelled.
     *
     * @param cancelled {@code true} to block the packet.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Utility method to check if the intercepted packet matches a specific class name.
     * <p>
     * This is particularly useful for identifying packets without requiring
     * direct imports of obfuscated NMS classes.
     *
     * @param packetClassName The simple name of the packet class (e.g., "PacketPlayInChat").
     * @return {@code true} if the packet class name matches, ignoring case.
     */
    public boolean isPacket(@NotNull String packetClassName) {
        return packet.getClass().getSimpleName().equalsIgnoreCase(packetClassName);
    }
}
