package uk.acronical.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for constructing and dispatching BungeeCord plugin messages.
 * <p>
 * This class utilises a fluent API to write data to a byte array, which is then
 * transmitted via a "carrier" player to the proxy.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class ProxyStream {

    private final ByteArrayDataOutput output;
    private final String subChannel;

    /**
     * Initialises a new {@link ProxyStream} for the specified sub-channel.
     *
     * @param subChannel The BungeeCord sub-channel (e.g., {@code "Connect"}, {@code "Message"}).
     */
    public ProxyStream(@NotNull String subChannel) {
        this.output = ByteStreams.newDataOutput();
        this.subChannel = subChannel;
        this.output.writeUTF(subChannel);
    }

    /**
     * Writes a {@link String} to the data stream.
     *
     * @param data The string to write.
     * @return The current {@link ProxyStream} instance.
     */
    public ProxyStream write(@NotNull String data) {
        this.output.writeUTF(data);
        return this;
    }

    /**
     * Writes an {@code int} to the data stream.
     *
     * @param data The integer to write.
     * @return The current {@link ProxyStream} instance.
     */
    public ProxyStream write(int data) {
        this.output.writeInt(data);
        return this;
    }

    /**
     * Writes a {@code boolean} to the data stream.
     *
     * @param data The boolean to write.
     * @return The current {@link ProxyStream} instance.
     */
    public ProxyStream write(boolean data) {
        this.output.writeBoolean(data);
        return this;
    }

    /**
     * Dispatches the constructed message to the proxy.
     * <p>
     * Note: A {@link Player} is required as a "carrier" to send the message
     * through their client-server connection.
     *
     * @param plugin  The plugin instance sending the message.
     * @param carrier The player to be utilised as the communication bridge.
     */
    public void send(@NotNull Plugin plugin, @NotNull Player carrier) {
        carrier.sendPluginMessage(plugin, "BungeeCord", this.output.toByteArray());
    }
}
