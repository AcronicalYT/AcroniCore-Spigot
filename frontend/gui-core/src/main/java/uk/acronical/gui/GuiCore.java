package uk.acronical.gui;

import org.bukkit.plugin.java.JavaPlugin;
import uk.acronical.common.LoggerUtils;
import uk.acronical.gui.listeners.GuiListeners;

public class GuiCore {

    private static boolean initialised = false;

    /**
     * Initialises the GUI core. This method should be called once during the plugin's onEnable() method.
     *
     * @param plugin The instance of the JavaPlugin to register events with.
     */
    public static void init(JavaPlugin plugin) {
        if (initialised) return;

        plugin.getServer().getPluginManager().registerEvents(new GuiListeners(), plugin);

        initialised = true;
        LoggerUtils.info("GUI core initialised successfully.");
    }
}
