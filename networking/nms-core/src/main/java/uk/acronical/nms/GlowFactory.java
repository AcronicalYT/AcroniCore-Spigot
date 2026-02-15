package uk.acronical.nms;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class GlowFactory {

    /**
     * Sets the glow effect for a target player as seen by a viewer player, using the specified glow color.
     *
     * @param viewer The player who will see the glow effect on the target player.
     * @param target The player who will have the glow effect applied to them.
     * @param glowColour The color of the glow effect to apply to the target player.
     * @throws ClassNotFoundException If the required NMS classes cannot be found, which may indicate that the plugin is not compatible with the server version.
     */
    public static void setGlow(Player viewer, Player target, ChatColor glowColour) throws ClassNotFoundException {
        try {
            Class<?> packetClass = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardTeam");
            Class<?> teamClass = ReflectionUtils.getNMSClass("ScoreboardTeam");
            Class<?> scoreboardClass = ReflectionUtils.getNMSClass("Scoreboard");

            Object scoreboard = scoreboardClass.getConstructor().newInstance();
            Object team = teamClass.getConstructor(scoreboardClass, String.class).newInstance(scoreboard, glowColour.name());

            Method addPlayer = teamClass.getMethod("getPlayerNameSet");
            Collection<String> players = (Collection<String>) addPlayer.invoke(team);
            players.add(target.getName());

            Constructor<?> packetConstructor = packetClass.getConstructor(teamClass, int.class);
            Object packet = packetConstructor.newInstance(team, 0);

            ReflectionUtils.sendPacket(viewer, packet);

            target.setGlowing(true);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Required NMS class not found. This plugin may not be compatible with your server version.", e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
