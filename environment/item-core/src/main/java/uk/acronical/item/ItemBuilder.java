package uk.acronical.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A utility class for creating {@link ItemStack} instances using a fluent API.
 * <p>
 * This builder simplifies item creation by allowing method chaining for common
 * tasks such as setting names, lore, and flags.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class ItemBuilder {

    protected final ItemStack item;
    protected final ItemMeta meta;

    /**
     * Initialises a new {@link ItemBuilder} with the specified {@link Material}.
     *
     * @param material The material type for the item.
     */
    public ItemBuilder(@NotNull Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * Initialises a new {@link ItemBuilder} based on an existing {@link ItemStack}.
     * <p>
     * Note: This constructor clones the provided item to ensure the original
     * instance remains unmodified.
     *
     * @param item The base item stack to copy.
     */
    public ItemBuilder(@NotNull ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    /**
     * Sets the display name of the item and applies colour codes.
     *
     * @param name The display name to set.
     * @return The current {@link ItemBuilder} instance.
     */
    public ItemBuilder name(@NotNull String name) {
        if (meta != null) meta.setDisplayName(StringUtils.colour(name));
        return this;
    }

    /**
     * Sets the lore lines for the item.
     *
     * @param lore The lines of text to display in the item's tooltip.
     * @return The current {@link ItemBuilder} instance.
     */
    public ItemBuilder lore(@NotNull String... lore) {
        if (meta != null) meta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Adds specific {@link ItemFlag}s to the item.
     *
     * @param flags The flags to apply (e.g., {@code HIDE_ATTRIBUTES}).
     * @return The current {@link ItemBuilder} instance.
     */
    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        if (meta != null) {
            for (ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }
        return this;
    }

    /**
     * Provides direct access to the {@link ItemMeta} for advanced modifications.
     * <p>
     * This is particularly useful for modifying specific meta subtypes like
     * {@link org.bukkit.inventory.meta.PotionMeta} or {@link org.bukkit.inventory.meta.SkullMeta}.
     *
     * @param consumer A functional interface to modify the meta.
     * @return The current {@link ItemBuilder} instance.
     */
    public ItemBuilder editMeta(@NotNull Consumer<ItemMeta> consumer) {
        if (meta != null) consumer.accept(meta);
        return this;
    }

    /**
     * Finalises the building process and applies the meta to the item.
     *
     * @return The constructed {@link ItemStack}.
     */
    @NotNull
    public ItemStack build() {
        if (meta != null) item.setItemMeta(meta);
        return item;
    }
}
