package uk.acronical.gui.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import uk.acronical.gui.guis.DefaultGui;

public class GuiListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof DefaultGui inventory) {
            event.setCancelled(true);
            inventory.handleMenu(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof DefaultGui inventory) {
            inventory.handleClose(event);
        }
    }
}
