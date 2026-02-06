package uk.acronical.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ReflectionUtils {

    private static final String version = Bukkit.getServer().getClass().getPackageName().split("\\.")[3];

    /**
     * Gets a class from the net.minecraft.server package for the current server version.
     *
     * @param className The name of the class to get (without the package).
     * @return The Class object for the specified class name.
     * @throws ClassNotFoundException If the class cannot be found in either the versioned or unversioned package.
     */
    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName("net.minecraft.server." + version + "." + className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("net.minecraft." + className);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Could not find NMS class: " + className, e);
            }
        }
    }

    /**
     * Gets a class from the org.bukkit.craftbukkit package for the current server version.
     *
     * @param className The name of the class to get (without the package).
     * @return The Class object for the specified class name.
     * @throws ClassNotFoundException If the class cannot be found in either the versioned or unversioned package.
     */
    public static Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("org.bukkit.craftbukkit." + className);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Could not find CraftBukkit class: " + className, e);
            }
        }
    }

    /**
     * Sends a packet to a player using reflection to access the player's connection.
     *
     * @param player The player to send the packet to.
     * @param packet The packet object to send (must be an instance of a class that extends net.minecraft.server.Packet).
     */
    public static void sendPacket(Player player, Object packet) {
        try {
            Object craftPlayer = getCraftBukkitClass("entity.CraftPlayer").cast(player);

            Object handle = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);

            Field connectionField = getFieldByType(handle.getClass(), getNMSClass("PlayerConnection"));
            assert connectionField != null;
            Object connection = connectionField.get(handle);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to get a field from a class by its type.
     *
     * @param target The class to search for the field.
     * @param fieldType The type of the field to find.
     * @return The Field object if found, or null if no matching field is found.
     */
    public static Field getFieldByType(Class<?> target, Class<?> fieldType) {
        for (Field field : target.getDeclaredFields()) {
            if (field.getType().equals(fieldType)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
