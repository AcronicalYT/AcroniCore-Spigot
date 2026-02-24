package uk.acronical.packet;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A service for injecting custom Netty handlers into the Minecraft network pipeline.
 * <p>
 * This utilises reflection to access the underlying {@link Channel} of a player,
 * allowing for the interception and manipulation of raw packets via {@link PacketListener}.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class PacketService implements Listener {

    private final String handlerName;
    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Initialises the {@link PacketService} with a unique handler name.
     *
     * @param plugin The plugin instance utilising this service.
     */
    public PacketService(@NotNull Plugin plugin) {
        this.handlerName = plugin.getName() + "_PacketInjector";
    }

    /**
     * Registers a new listener to intercept packet traffic.
     *
     * @param listener The {@link PacketListener} to add.
     */
    public void registerListener(@NotNull PacketListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters an existing packet listener.
     *
     * @param listener The {@link PacketListener} to remove.
     */
    public void unregisterListener(@NotNull PacketListener listener) {
        listeners.remove(listener);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        uninject(event.getPlayer());
    }

    /**
     * Injects the duplex handler into the player's Netty pipeline.
     */
    private void inject(Player player) {
        try {
            Channel channel = getChannel(player);
            if (channel == null) return;

            if (channel.pipeline().get(handlerName) != null) return;

            ChannelDuplexHandler handler = new ChannelDuplexHandler() {
                @Override
                public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
                    PacketEvent event = new PacketEvent(player, packet);

                    for (PacketListener listener : listeners) listener.onPacketSend(event);

                    if (event.isCancelled()) return;

                    super.write(context, event.getPacket(), promise);
                }

                @Override
                public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                    PacketEvent event = new PacketEvent(player, packet);

                    for (PacketListener listener : listeners) listener.onPacketReceive(event);

                    if (event.isCancelled()) return;

                    super.channelRead(context, event.getPacket());
                }
            };

            channel.pipeline().addBefore("packet_handler", handlerName, handler);
        } catch (Exception e) {
            LoggerUtils.severe("Failed to inject packet listener for " + player.getName());
            LoggerUtils.severe(e.getMessage());
        }
    }

    /**
     * Removes the custom handler from the player's pipeline to prevent memory leaks.
     */
    private void uninject(Player player) {
        try {
            Channel channel = getChannel(player);

            if (channel != null && channel.pipeline().get(handlerName) != null) channel.pipeline().remove(handlerName);
        } catch (Exception ignored) {}
    }

    /**
     * Navigates the internal Minecraft server classes to find the Netty {@link Channel}.
     */
    private Channel getChannel(Player player) throws Exception {
        Method getHandle = player.getClass().getMethod("getHandle");
        Object serverPlayer = getHandle.invoke(player);

        Field connectionField = getFieldByType(serverPlayer.getClass(), "ServerGamePacketListenerImpl");
        Object connection = connectionField.get(serverPlayer);

        Field networkManagerField = getFieldByType(connection.getClass(), "Connection");
        Object networkManager = networkManagerField.get(connection);

        Field channelField = getFieldByType(networkManager.getClass(), "Channel");
        return (Channel) channelField.get(networkManager);
    }

    /**
     * Helper to find fields by their type name, aiding in version-independent reflection.
     */
    private Field getFieldByType(Class<?> clazz, String simpleTypeName) {
        for (Field field : clazz.getFields()) if (field.getType().getSimpleName().equals(simpleTypeName)) return field;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getSimpleName().equals(simpleTypeName)) {
                field.setAccessible(true);
                return field;
            }
        }

        throw new RuntimeException("Could not find field of type " + simpleTypeName + " in " + clazz.getSimpleName());
    }
}
