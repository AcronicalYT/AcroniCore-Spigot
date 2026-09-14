package uk.acronical.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import uk.acronical.task.TaskManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A highly concurrent service for queuing and processing block updates over time.
 * <p>
 * This service allows asynchronous threads to queue massive structural changes
 * (e.g., schematic pasting or arena resets) while the main thread processes
 * them in controlled batches to maintain server TPS.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class BlockService {

    private final Queue<BlockUpdate> updateQueue = new ConcurrentLinkedQueue<>();

    private int blocksPerTick = 500;

    /**
     * Initialises the BlockService and starts the processing task.
     *
     * @param taskManager The task manager to run the synchronised processing loop.
     */
    public BlockService(@NotNull TaskManager taskManager) {
        taskManager.sync(this::processQueue, 1L, 1L);
    }

    /**
     * Queues a block update with physics disabled by default.
     *
     * @param location  The target location.
     * @param blockData The new block data.
     */
    public void queue(@NotNull Location location, @NotNull BlockData blockData) {
        updateQueue.add(new BlockUpdate(location, blockData, false));
    }

    /**
     * Queues a block update with explicitly defined physics behaviour.
     *
     * @param location     The target location.
     * @param blockData    The new block data.
     * @param applyPhysics Whether to trigger adjacent block updates.
     */
    public void queue(@NotNull Location location, @NotNull BlockData blockData, boolean applyPhysics) {
        updateQueue.add(new BlockUpdate(location, blockData, applyPhysics));
    }

    /**
     * Sets the maximum number of blocks placed per tick.
     *
     * @param blocksPerTick The limit (must be greater than zero).
     */
    public void setBlocksPerTick(int blocksPerTick) {
        if (blocksPerTick <= 0) throw new IllegalArgumentException("Blocks per tick must be greater than zero.");
        this.blocksPerTick = blocksPerTick;
    }

    /**
     * Retrieves the current number of pending block updates.
     *
     * @return The queue size.
     */
    public int getQueueSize() {
        return updateQueue.size();
    }

    /**
     * Processes the queue up to the defined blocksPerTick limit.
     */
    private void processQueue() {
        if (updateQueue.isEmpty()) return;

        for (int i = 0; i < blocksPerTick; i++) {
            BlockUpdate update = updateQueue.poll();
            if (update == null) break;

            Block block = update.getLocation().getBlock();

            block.setBlockData(update.getBlockData(), update.shouldApplyPhysics());
        }
    }
}
