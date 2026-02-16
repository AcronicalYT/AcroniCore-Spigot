package uk.acronical.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.pdc.wrapper.BlockPdc;
import uk.acronical.pdc.wrapper.EntityPdc;
import uk.acronical.pdc.wrapper.ItemPdc;

/**
 * A service class providing a unified interface for interacting with Persistent Data Containers.
 * <p>
 * This service simplifies the management of {@link NamespacedKey} instances and provides
 * specialised wrappers for persistent data on {@link ItemStack}s, {@link Entity} instances,
 * and {@link Block}s.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class PdcService {

    private final Plugin plugin;

    /**
     * Initialises the {@link PdcService} with the providing {@link Plugin} instance.
     *
     * @param plugin The plugin responsible for owning the persistent data keys.
     */
    public PdcService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new {@link NamespacedKey} using the service's plugin namespace.
     *
     * @param key The unique string identifier for the data.
     * @return A {@link NamespacedKey} associated with this plugin.
     */
    @NotNull
    public NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(plugin, key);
    }

    /**
     * Creates a persistent data wrapper for an {@link ItemStack}.
     *
     * @param item The item stack to wrap.
     * @return A new {@link ItemPdc} instance.
     */
    @NotNull
    public ItemPdc of(@NotNull ItemStack item) {
        return new ItemPdc(this, item);
    }

    /**
     * Creates a persistent data wrapper for an {@link Entity}.
     *
     * @param entity The entity to wrap.
     * @return A new {@link EntityPdc} instance.
     */
    @NotNull
    public EntityPdc of(@NotNull Entity entity) {
        return new EntityPdc(this, entity);
    }

    /**
     * Creates a persistent data wrapper for a {@link Block}.
     *
     * @param block The block to wrap.
     * @return A new {@link BlockPdc} instance.
     * @throws IllegalArgumentException If the block does not support persistent data.
     */
    public BlockPdc of(Block block) {
        return new BlockPdc(this, block);
    }
}