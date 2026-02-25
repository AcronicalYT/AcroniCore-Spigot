package uk.acronical.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A mathematical representation of a 3D rectangular region within a world.
 * <p>
 * This class facilitates spatial checks, block iteration, and volume calculations
 * for specific areas. It utilises {@link UUID} for world identification to maintain
 * persistence safety.
 *
 * @author Acronical
 * @since 1.0.4
 */
public class Cuboid {

    private final UUID worldId;
    private final int minX, maxX, minY, maxY, minZ, maxZ;

    /**
     * Initialises a new {@link Cuboid} defined by two opposite corner points.
     *
     * @param startPoint The first corner of the region.
     * @param endPoint   The opposite corner of the region.
     * @throws IllegalArgumentException If worlds are null or mismatched.
     */
    public Cuboid(@NotNull Location startPoint, @NotNull Location endPoint) {
        if (startPoint.getWorld() == null || endPoint.getWorld() == null) throw new IllegalArgumentException("The world is either not loaded, or does not exist.");
        if (!startPoint.getWorld().equals(endPoint.getWorld())) throw new IllegalArgumentException("Both points must exist in the same world.");

        this.worldId = startPoint.getWorld().getUID();

        this.minX = Math.min(startPoint.getBlockX(), endPoint.getBlockX());
        this.minY = Math.min(startPoint.getBlockY(), endPoint.getBlockY());
        this.minZ = Math.min(startPoint.getBlockZ(), endPoint.getBlockZ());

        this.maxX = Math.max(startPoint.getBlockX(), endPoint.getBlockX());
        this.maxY = Math.max(startPoint.getBlockY(), endPoint.getBlockY());
        this.maxZ = Math.max(startPoint.getBlockZ(), endPoint.getBlockZ());
    }

    /**
     * Retrieves the {@link World} associated with this cuboid.
     *
     * @return The world instance, or {@code null} if the world is not loaded.
     */
    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    /**
     * Checks if a specific {@link Location} resides within the cuboid boundaries.
     *
     * @param location The location to check.
     * @return {@code true} if the location is inside; otherwise {@code false}.
     */
    public boolean contains(@NotNull Location location) {
        if (location.getWorld() == null || !location.getWorld().getUID().equals(worldId)) return false;

        return location.getX() >= minX && location.getX() <= maxX && location.getY() >= minY && location.getY() <= maxY + 1 && location.getZ() >= minZ && location.getZ() <= maxZ + 1;
    }

    /**
     * Checks if a player is currently within the cuboid boundaries.
     *
     * @param player The player to check.
     * @return {@code true} if the player is inside.
     */
    public boolean contains(@NotNull Player player) {
        return contains(player.getLocation());
    }

    /**
     * Calculates the geometric centre of the cuboid.
     *
     * @return A new {@link Location} representing the centre point.
     */
    @NotNull
    public Location getCenter() {
        return new Location(getWorld(), minX + (maxX - minX) / 2.0 + 0.5, minY + (maxY - minY) / 2.0 + 0.5, minZ + (maxZ - minZ) / 2.0 + 0.5);
    }

    /**
     * Iterates through all blocks within the cuboid.
     * <p>
     * Warning: Utilising this on very large cuboids may result in performance issues
     * or a {@link OutOfMemoryError}.
     *
     * @return A list of all {@link Block} objects within the region.
     */
    @NotNull
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();

        World world = getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    /**
     * Calculates the total volume of the cuboid in blocks.
     *
     * @return The total block count.
     */
    public int getVolume() {
        return (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
    }
}
