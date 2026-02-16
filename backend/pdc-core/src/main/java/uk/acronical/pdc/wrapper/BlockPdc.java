package uk.acronical.pdc.wrapper;

import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import uk.acronical.pdc.PdcService;

/**
 * A specialised wrapper for managing persistent data on {@link Block} instances.
 * <p>
 * Note that only blocks possessing a {@link TileState} (such as chests, signs,
 * or furnaces) can store persistent data.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class BlockPdc extends PdcWrapper<Block> {

    private final TileState tileState;

    /**
     * Initialises a new {@link BlockPdc} wrapper for the specified block.
     *
     * @param pdcService The service used for {@link org.bukkit.NamespacedKey} generation.
     * @param block      The {@link Block} to manage.
     * @throws IllegalArgumentException If the block does not support persistent data.
     */
    public BlockPdc(@NotNull PdcService pdcService, @NotNull Block block) {
        super(pdcService, block);
        if (!(block.getState() instanceof TileState)) throw new IllegalArgumentException("Block at " + block.getLocation() + " is not a TileState and cannot store PDC data.");
        this.tileState = (TileState) block.getState();
    }

    /**
     * Retrieves the {@link PersistentDataContainer} from the block's tile state.
     *
     * @return The underlying data container.
     */
    @Override
    @NotNull
    protected PersistentDataContainer getContainer() {
        return tileState.getPersistentDataContainer();
    }

    /**
     * Finalises changes by updating the block's state.
     * <p>
     * Similar to item metadata, block data is only persisted to the world once
     * {@link TileState#update()} is called.
     */
    @Override
    protected void save() {
        tileState.update();
    }
}