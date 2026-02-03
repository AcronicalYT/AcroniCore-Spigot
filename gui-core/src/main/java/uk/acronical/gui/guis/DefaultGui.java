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

public abstract class DefaultGui implements InventoryHolder {

    /**
     * The inventory object of the GUI.
     */
    protected Inventory inventory;

    /**
     * The player for whom the GUI is created.
     */
    protected final Player player;

    /**
     * The title of the GUI.
     */
    protected final String title;

    /**
     * The size of the GUI.
     */
    protected final int size;

    /**
     * Constructor for DefaultGui.
     * @param player The player for whom the GUI is created.
     * @param title The title of the GUI.
     * @param size The size of the GUI.
     */
    public DefaultGui(Player player, String title, int size) {
        this.player = player;
        this.title = StringUtils.colour(title);
        this.size = size;
    }

    /**
     * Gets the title of the GUI.
     * @return The title of the GUI.
     */
    public String getTitle() { return title; }

    /**
     * Gets the size of the GUI.
     * @return The size of the GUI.
     */
    public int getSize() { return size; }

    /**
     * Handles menu interactions.
     * @param event The InventoryClickEvent.
     */
    public abstract void handleMenu(InventoryClickEvent event);

    /**
     * Sets the menu items in the GUI.
     */
    public abstract void setMenuItems();

    /**
     * Opens the GUI for the player.
     */
    public void open() {
        inventory = player.getServer().createInventory(this, size, title);
        setMenuItems();
        player.openInventory(inventory);
    }

    /**
     * Creates a GUI item with the specified material, display name, and lore.
     * @param material The material (type) of the item.
     * @param displayName The display name of the item.
     * @param lore The lore (description) of the item.
     * @return The created ItemStack.
     */
    public ItemStack createGuiItem(Material material, String displayName, String... lore) {
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
     * Gets the inventory of the GUI.
     * @return The inventory object of the GUI.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Handles the closing of the inventory. (Optional to override)
     * @param event The InventoryCloseEvent.
     */
    public void handleClose(InventoryCloseEvent event) { }

    /**
     * Handles click events in the inventory.
     * @param event The InventoryClickEvent.
     */
    public void handleClick(InventoryClickEvent event) {
        handleMenu(event);
    }
}