package uk.acronical.proxy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A centralised service for managing cross-server communication via the BungeeCord messaging channel.
 * <p>
 * This service facilitates common proxy actions such as player redirection and data requests,
 * while providing a registration system for handling asynchronous responses.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class ProxyService implements PluginMessageListener {

    private final Plugin plugin;
    private final Map<String, Consumer<DataInputStream>> listeners = new HashMap<>();

    /**
     * Initialises the {@link ProxyService} and registers the required plugin channels.
     *
     * @param plugin The {@link Plugin} instance responsible for the channels.
     */
    public ProxyService(@NotNull Plugin plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    /**
     * Redirects a player to a different server within the proxy network.
     *
     * @param player     The player to be moved.
     * @param serverName The target server name as defined in the proxy configuration.
     */
    public void connect(@NotNull Player player, @NotNull String serverName) {
         new ProxyStream("Connect").write(serverName).send(this.plugin, player);
    }

    /**
     * Disconnects a player from the network with a specified reason.
     *
     * @param player The player to kick.
     * @param reason The message to display upon disconnection.
     */
    public void kick(@NotNull Player player, @NotNull String reason) {
        new ProxyStream("KickPlayer").write(player.getName()).write(reason).send(this.plugin, player);
    }

    /**
     * Requests the IP address of a player from the proxy.
     * <p>
     * The response should be handled by registering a listener via {@link #onResponse}.
     *
     * @param player The player whose IP is requested.
     */
    public void requestIP(@NotNull Player player) {
        new ProxyStream("IP").send(this.plugin, player);
    }

    /**
     * Sends a simple data packet to a specified sub-channel.
     *
     * @param player     The carrier player for the message.
     * @param subChannel The target BungeeCord sub-channel.
     * @param data       The string data to transmit.
     */
    public void send(@NotNull Player player, @NotNull String subChannel, @NotNull String data) {
        new ProxyStream(subChannel).write(data).send(this.plugin, player);
    }

    /**
     * Sends multiple string arguments to a specified sub-channel.
     *
     * @param player     The carrier player for the message.
     * @param subChannel The target BungeeCord sub-channel.
     * @param data       The sequence of strings to transmit.
     */
    public void send(@NotNull Player player, @NotNull String subChannel, @NotNull String... data) {
        ProxyStream stream = new ProxyStream(subChannel);
        for (String datum : data) {
            stream.write(datum);
        }
        stream.send(this.plugin, player);
    }

    /**
     * Registers a handler to process incoming data from a specific proxy sub-channel.
     *
     * @param subChannel The sub-channel to monitor.
     * @param handler    A {@link Consumer} providing the {@link DataInputStream} of the response.
     */
    public void onResponse(@NotNull String subChannel, @NotNull Consumer<DataInputStream> handler) {
        this.listeners.put(subChannel, handler);
    }

    /**
     * Internal listener for processing incoming plugin messages.
     */
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("BungeeCord")) return;

        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(message))) {
            String subChannel = inputStream.readUTF();

            if (this.listeners.containsKey(subChannel)) {
                this.listeners.get(subChannel).accept(inputStream);
            }
        } catch (Exception e) {
            LoggerUtils.severe("Failed to process incoming proxy message: " + e.getMessage());
        }
    }
}
