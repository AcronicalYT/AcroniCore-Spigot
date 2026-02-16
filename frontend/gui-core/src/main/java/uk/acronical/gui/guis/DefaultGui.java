package uk.acronical.gui.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.StringUtils;

import java.util.Arrays;

/**
 * An abstract base class representing a custom GUI.
 * <p>
 * This class implements {@link InventoryHolder}, allowing for easy identification
 * of custom menus during inventory events. It provides the foundation for
 * creating, opening, and handling interactions within a GUI.
 *
 * @author Acronical
 * @since 1.0.0
 */
public abstract class DefaultGui implements InventoryHolder {

    protected Inventory inventory;
    protected final Player player;
    protected final String title;
    protected final int size;

    /**
     * Initialises a new {@link DefaultGui}.
     *
     * @param player The player for whom the GUI is created.
     * @param title  The display title (supports colour codes).
     * @param size   The number of slots in the inventory.
     */
    public DefaultGui(@NotNull Player player, @NotNull String title, int size) {
        this.player = player;
        this.title = StringUtils.colour(title);
        this.size = size;
    }

    /**
     * @return The colour-translated title of the GUI.
     */
    public String getTitle() { return title; }

    /**
     * @return The total slot count of the GUI.
     */
    public int getSize() { return size; }

    /**
     * Handles specific menu interactions defined by the subclass.
     *
     * @param event The {@link InventoryClickEvent} triggered by the player.
     */
    public abstract void handleMenu(InventoryClickEvent event);

    /**
     * Populates the inventory with items.
     * <p>
     * This method is invoked automatically within the {@link #open()} method
     * before the inventory is shown to the player.
     */
    public abstract void setMenuItems();

    /**
     * Creates the inventory and opens it for the player.
     */
    public void open() {
        inventory = player.getServer().createInventory(this, size, title);
        setMenuItems();
        player.openInventory(inventory);
    }

    /**
     * Creates a colour-translated {@link ItemStack} for use in the GUI.
     *
     * @param material    The {@link Material} type of the item.
     * @param displayName The display name (supports colour codes).
     * @param lore        The lore lines (supports colour codes).
     * @return The constructed {@link ItemStack}.
     */
    public ItemStack createGuiItem(@NotNull Material material, @NotNull String displayName, @NotNull String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(StringUtils.colour(displayName));
            meta.setLore(Arrays.stream(lore).map(StringUtils::colour).toList());
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Retrieves the {@link Inventory} associated with this holder.
     *
     * @return The {@link Inventory} object.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Handles the closing of the inventory.
     * <p>
     * This is an optional hook that subclasses can override to perform
     * logic when the player exits the GUI.
     *
     * @param event The {@link InventoryCloseEvent}.
     */
    public void handleClose(InventoryCloseEvent event) { }

    /**
     * Routes inventory click events to the {@link #handleMenu(InventoryClickEvent)} method.
     *
     * @param event The {@link InventoryClickEvent}.
     */
    public void handleClick(InventoryClickEvent event) {
        handleMenu(event);
    }
}