package uk.acronical.nms;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * A factory class for manipulating the glowing effect through NMS packets.
 * <p>
 * This utility allows for per-viewer glow colours by creating temporary,
 * client-side scoreboard teams via the {@code PacketPlayOutScoreboardTeam} packet.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class GlowFactory {

    /**
     * Sets a viewer-specific glow colour for a target player.
     * <p>
     * Note: This method utilises reflection to access internal server classes.
     * If the required NMS classes are missing, it suggests the server version
     * is incompatible with this implementation.
     *
     * @param viewer     The player who will perceive the glow effect.
     * @param target     The player who will appear to be glowing.
     * @param glowColour The {@link ChatColor} to be applied to the glow outline.
     * @throws ClassNotFoundException If internal NMS classes cannot be found.
     */
    public static void setGlow(@NotNull Player viewer, @NotNull Player target, @NotNull ChatColor glowColour) throws ClassNotFoundException {
        try {
            Class<?> packetClass = ReflectionUtils.getNMSClass("PacketPlayOutScoreboardTeam");
            Class<?> teamClass = ReflectionUtils.getNMSClass("ScoreboardTeam");
            Class<?> scoreboardClass = ReflectionUtils.getNMSClass("Scoreboard");

            Object scoreboard = scoreboardClass.getConstructor().newInstance();
            Object team = teamClass.getConstructor(scoreboardClass, String.class).newInstance(scoreboard, glowColour.name());

            Method addPlayer = teamClass.getMethod("getPlayerNameSet");
            @SuppressWarnings("unchecked")
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
