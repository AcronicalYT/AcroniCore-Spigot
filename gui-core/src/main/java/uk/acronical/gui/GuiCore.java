package uk.acronical.gui;

import org.bukkit.plugin.java.JavaPlugin;
import uk.acronical.gui.listeners.GuiListeners;

public class GuiCore {

    private static boolean initialised = false;

    public static void init(JavaPlugin plugin) {
        if (initialised) return;

        plugin.getServer().getPluginManager().registerEvents(new GuiListeners(), plugin);

        initialised = true;
        plugin.getLogger().info("GuiCore initialized.");
    }
}
