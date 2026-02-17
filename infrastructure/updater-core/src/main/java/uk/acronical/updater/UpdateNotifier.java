package uk.acronical.updater;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.StringUtils;

/**
 * A listener responsible for notifying authorised players of available plugin updates.
 * <p>
 * This class monitors {@link PlayerJoinEvent} and dispatches a formatted chat message
 * if a newer version has been detected by the {@link UpdateChecker}.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class UpdateNotifier implements Listener {

    private final Plugin plugin;
    private final UpdateChecker.UpdateResult result;
    private final String permission;

    /**
     * Initialises a new {@link UpdateNotifier}.
     *
     * @param plugin     The plugin instance associated with the update.
     * @param result     The result of the version check.
     * @param permission The permission required to see the notification (may be {@code null}).
     */
    public UpdateNotifier(@NotNull Plugin plugin, @NotNull UpdateChecker.UpdateResult result, @Nullable String permission) {
        this.plugin = plugin;
        this.result = result;
        this.permission = permission;
    }

    /**
     * Checks joining players for appropriate permissions and sends the update notification.
     * <p>
     * Operators are notified by default, even if they lack the specified permission node.
     *
     * @param event The player join event.
     */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if ((permission != null && player.hasPermission(permission)) || player.isOp()) {
            if (result.hasUpdate()) {
                player.sendMessage(StringUtils.colour("-------------------------------------------------"));
                player.sendMessage(StringUtils.colour("&aAn update is available for &e" + plugin.getName() + "&a!"));
                player.sendMessage(StringUtils.colour("&aCurrent version: &e" + result.oldVersion()));
                player.sendMessage(StringUtils.colour("&aLatest version: &e" + result.newVersion()));
                player.sendMessage(StringUtils.colour("&aDownload it here: &e" + result.downloadUrl()));
                player.sendMessage(StringUtils.colour("-------------------------------------------------"));
            }
        }
    }
}
