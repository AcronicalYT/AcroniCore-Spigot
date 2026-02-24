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
import uk.acronical.common.LoggerUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketService implements Listener {

    private final String handlerName;
    private final List<PacketListener> listeners = new CopyOnWriteArrayList<>();

    public PacketService(Plugin plugin) {
        this.handlerName = plugin.getName() + "_PacketInjector";
    }

    public void registerListener(PacketListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(PacketListener listener) {
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

    private void uninject(Player player) {
        try {
            Channel channel = getChannel(player);

            if (channel != null && channel.pipeline().get(handlerName) != null) channel.pipeline().remove(handlerName);
        } catch (Exception ignored) {}
    }

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
