package uk.acronical.block;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a queued block modification within the world.
 * <p>
 * This immutable model encapsulates the target location, the new block data,
 * and whether physical updates (like gravity or redstone triggering) should
 * occur when the block is placed.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class BlockUpdate {

    private final Location location;
    private final BlockData blockData;
    private final boolean applyPhysics;

    /**
     * Initialises a new block update request.
     *
     * @param location     The specific location in the world to modify.
     * @param blockData    The new block state and material to apply.
     * @param applyPhysics Whether to trigger block physics (e.g., adjacent block updates).
     */
    public BlockUpdate(@NotNull Location location, @NotNull BlockData blockData, boolean applyPhysics) {
        this.location = location;
        this.blockData = blockData;
        this.applyPhysics = applyPhysics;
    }

    /**
     * Retrieves the target location for this update.
     *
     * @return The block location.
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Retrieves the data to be applied to the block.
     *
     * @return The target block data.
     */
    @NotNull
    public BlockData getBlockData() {
        return blockData;
    }

    /**
     * Checks if physics should be applied during the update.
     * <p>
     * Disabling physics is highly recommended for large-scale structure
     * generation to prevent performance degradation and block breaking.
     *
     * @return True if physics should be applied, false otherwise.
     */
    public boolean shouldApplyPhysics() {
        return applyPhysics;
    }
}
