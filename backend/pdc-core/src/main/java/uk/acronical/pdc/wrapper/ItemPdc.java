package uk.acronical.pdc.wrapper;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import uk.acronical.pdc.PdcService;

/**
 * A specialised wrapper for managing persistent data on {@link ItemStack} instances.
 * <p>
 * This class handles the retrieval and re-application of {@link ItemMeta} to ensure
 * that data modifications are correctly saved to the item.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class ItemPdc extends PdcWrapper<ItemStack> {

    private final ItemMeta itemMeta;

    /**
     * Initialises a new {@link ItemPdc} wrapper for the specified item.
     *
     * @param pdcService The service used for {@link org.bukkit.NamespacedKey} generation.
     * @param item       The {@link ItemStack} to manage.
     */
    public ItemPdc(@NotNull PdcService pdcService, @NotNull ItemStack item) {
        super(pdcService, item);
        this.itemMeta = item.getItemMeta();
    }

    /**
     * Retrieves the {@link PersistentDataContainer} from the item's meta.
     *
     * @return The underlying data container.
     */
    @Override
    @NotNull
    protected PersistentDataContainer getContainer() {
        return itemMeta.getPersistentDataContainer();
    }

    /**
     * Re-applies the modified {@link ItemMeta} to the {@link ItemStack}.
     * <p>
     * In the Bukkit API, changes to an item's persistent data are only saved
     * once the meta is explicitly set back onto the item stack.
     */
    @Override
    protected void save() {
        holder.setItemMeta(itemMeta);
    }
}
