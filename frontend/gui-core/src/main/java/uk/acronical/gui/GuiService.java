package uk.acronical.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.gui.guis.DefaultGui;

/**
 * A centralised service for managing GUI interactions and inventory events.
 * <p>
 * This service utilises the Bukkit {@link org.bukkit.plugin.ServicesManager} to facilitate
 * cross-plugin sharing of GUI logic, ensuring that the most recent version of the
 * framework handles inventory events.
 *
 * @author Acronical
 * @since 1.0.2
 */
public class GuiService implements Listener {

    private final Plugin plugin;
    private boolean isInitialised = false;

    /**
     * Initialises the {@link GuiService} and registers it as a listener.
     *
     * @param plugin The plugin instance responsible for registration.
     */
    public GuiService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers the {@link GuiService} as a listener and marks it as initialised.
     * <p>
     * This method should be called during the plugin's enable phase to ensure that
     * inventory events are properly handled. If the service has already been initialised,
     * a warning will be logged to prevent duplicate registrations.
     */
    public void init() {
        if (isInitialised) {
            LoggerUtils.warn("GuiService has already been initialised. Plugin " + plugin.getName() + " has attempted re-initialisation...");
            return;
        }

        isInitialised = true;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Shuts down the {@link GuiService} and marks it as uninitialised.
     * <p>
     * This method should be called during the plugin's disable phase to clean up resources.
     * If the service has not been initialised, a warning will be logged to prevent
     * attempts to shut down an uninitialised service.
     */
    public void shutdown() {
        if (!isInitialised) {
            LoggerUtils.warn("GuiService has not been initialised. Plugin " + plugin.getName() + " has attempted to shutdown an uninitialised service...");
            return;
        }

        isInitialised = false;
    }

    /**
     * Checks if the {@link GuiService} has been initialised.
     *
     * @return true if the service is initialised, false otherwise.
     */
    public boolean getInitialised() {
        return isInitialised;
    }

    /**
     * Intercepts inventory clicks and dispatches them to the associated {@link DefaultGui}.
     *
     * @param event The inventory click event.
     */
    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof DefaultGui gui)) return;
        event.setCancelled(true);
        gui.handleClick(event);
    }

    /**
     * Dispatches inventory close events to the associated {@link DefaultGui}.
     *
     * @param event The inventory close event.
     */
    @EventHandler
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof DefaultGui gui)) return;
        gui.handleClose(event);
    }
}
