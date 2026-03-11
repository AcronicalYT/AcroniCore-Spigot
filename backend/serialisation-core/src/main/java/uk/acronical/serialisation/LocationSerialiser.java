package uk.acronical.serialisation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility for converting {@link Location} objects to and from a serialised string format.
 * <p>
 * This class utilises a semicolon-delimited format (world;x;y;z;yaw;pitch) which is
 * ideal for storage in flat-files or SQL databases where human readability is preferred.
 *
 * @author Acronical
 * @since 1.0.4
 */
public class LocationSerialiser {

    /**
     * Serialises a {@link Location} into a semicolon-delimited string.
     *
     * @param location The location to serialise.
     * @return A string in the format {@code world;x;y;z;yaw;pitch}, or an empty string if the world is null.
     */
    @NotNull
    public static String toString(@NotNull Location location) {
        if (location.getWorld() == null) return "";

        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    /**
     * Deserialises a {@link Location} from a semicolon-delimited string.
     *
     * @param string The serialised location string.
     * @return The resulting {@link Location} object, or {@code null} if the input is empty.
     * @throws IllegalArgumentException If the string format is invalid or the world is not loaded.
     */
    @Nullable
    public static Location fromString(@NotNull String string) {
        if (string.trim().isEmpty()) return null;

        String[] parts = string.split(";");

        if (parts.length != 6) throw new IllegalArgumentException("Invalid location string format: " + string);

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) throw new IllegalArgumentException("World '" + parts[0] + "' is not loaded or does not exist.");

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse coordinates from string: " + string, e);
        }
    }
}
