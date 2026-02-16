package uk.acronical.gui;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;
import uk.acronical.gui.listeners.GuiListeners;

/**
 * The central management class for the GUI system.
 * <p>
 * This class handles the one-time setup required for the GUI framework to function,
 * such as registering the necessary {@link org.bukkit.event.Listener} instances.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class GuiCore {

    private static boolean initialised = false;

    /**
     * Initialises the GUI framework.
     * <p>
     * This method should be invoked once during the {@link Plugin#onEnable()} phase.
     * Subsequent calls will be ignored to prevent duplicate event registrations.
     *
     * @param plugin The {@link Plugin} instance used to register events.
     */
    public static void init(@NotNull Plugin plugin) {
        if (initialised) return;

        plugin.getServer().getPluginManager().registerEvents(new GuiListeners(), plugin);

        initialised = true;
        LoggerUtils.info("GUI core initialised successfully.");
    }
}
