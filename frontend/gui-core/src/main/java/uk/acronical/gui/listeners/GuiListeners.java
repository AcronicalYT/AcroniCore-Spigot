package uk.acronical.gui.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import uk.acronical.gui.guis.DefaultGui;

/**
 * Listens for inventory events to facilitate {@link DefaultGui} interactions.
 * <p>
 * This class intercepts clicks and closure events, routing them to the
 * appropriate {@link DefaultGui} instance if the inventory holder matches.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class GuiListeners implements Listener {

    /**
     * Handles clicks within inventories managed by the GUI framework.
     * <p>
     * If the clicked inventory is a {@link DefaultGui}, the event is
     * automatically cancelled to prevent item movement, and the click
     * is passed to the GUI's handler.
     *
     * @param event The {@link InventoryClickEvent} to process.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof DefaultGui inventory) {
            event.setCancelled(true);
            inventory.handleClick(event);
        }
    }

    /**
     * Handles the closure of inventories managed by the GUI framework.
     * <p>
     * If the inventory belongs to a {@link DefaultGui}, the closure
     * logic defined in the GUI's {@link DefaultGui#handleClose(InventoryCloseEvent)}
     * method is executed.
     *
     * @param event The {@link InventoryCloseEvent} to process.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof DefaultGui inventory) {
            inventory.handleClose(event);
        }
    }
}
