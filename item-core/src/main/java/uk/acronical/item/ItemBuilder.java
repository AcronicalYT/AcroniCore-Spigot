package uk.acronical.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ItemBuilder {

    protected final ItemStack item;
    protected final ItemMeta meta;

    /**
     * Creates a new ItemBuilder for a given Material.
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * Creates a new ItemBuilder for a given ItemStack.
     */
    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    /**
     * Sets the display name of the item.
     * @param name The display name to set.
     * @return The current ItemBuilder instance.
     */
    public ItemBuilder name(String name) {
        if (meta != null) meta.setDisplayName(name);
        return this;
    }

    /**
     * Sets the lore of the item.
     * @param lore The lore lines to set.
     * @return The current ItemBuilder instance.
     */
    public ItemBuilder lore(String... lore) {
        if (meta != null) meta.setLore(java.util.Arrays.asList(lore));
        return this;
    }

    /**
     * Sets the item flags of the item.
     * @param flags The item flags to set.
     * @return The current ItemBuilder instance.
     */
    public ItemBuilder flags(ItemFlag... flags) {
        if (meta != null) {
            for (ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }
        return this;
    }

    /**
     * Allows for complex modifications to the ItemMeta via a Consumer.
     * @param consumer The consumer that modifies the ItemMeta.
     * @return The current ItemBuilder instance.
     */
    public ItemBuilder editMeta(Consumer<ItemMeta> consumer) {
        if (meta != null) consumer.accept(meta);
        return this;
    }

    /**
     * Builds and returns the final ItemStack.
     * @return The constructed ItemStack.
     */
    public ItemStack build() {
        if (meta != null) item.setItemMeta(meta);
        return item;
    }
}
