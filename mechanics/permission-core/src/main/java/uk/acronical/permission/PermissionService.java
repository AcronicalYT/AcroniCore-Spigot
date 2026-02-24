package uk.acronical.permission;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service for managing transient {@link PermissionAttachment} objects for players.
 * <p>
 * This utility facilitates the granting and revoking of permissions during a
 * player's session, ensuring that references are cleaned up upon disconnection
 * to prevent memory leaks.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class PermissionService implements Listener {

    private final Plugin plugin;
    private final Map<UUID, PermissionAttachment> attachmentMap = new ConcurrentHashMap<>();

    /**
     * Initialises the {@link PermissionService}.
     *
     * @param plugin The plugin instance responsible for the attachments.
     */
    public PermissionService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieves or creates a {@link PermissionAttachment} for the specified player.
     *
     * @param player The player to attach permissions to.
     * @return An existing or newly created attachment.
     */
    @NotNull
    private PermissionAttachment getAttachment(@NotNull Player player) {
        return attachmentMap.computeIfAbsent(player.getUniqueId(), uuid -> player.addAttachment(plugin));
    }

    /**
     * Grants a transient permission to a player for the duration of their session.
     * <p>
     * This method also triggers a command tree refresh to update client-side
     * tab completion for newly accessible commands.
     *
     * @param player     The recipient of the permission.
     * @param permission The permission node to grant.
     */
    public void grantTransient(@NotNull Player player, @NotNull String permission) {
        PermissionAttachment attachment = getAttachment(player);
        attachment.setPermission(permission, true);
        player.updateCommands();
    }

    /**
     * Revokes a transient permission previously granted to the player.
     *
     * @param player     The player whose permission should be removed.
     * @param permission The permission node to unset.
     */
    public void revokeTransient(@NotNull Player player, @NotNull String permission) {
        if (!attachmentMap.containsKey(player.getUniqueId())) return;

        PermissionAttachment attachment = attachmentMap.get(player.getUniqueId());
        attachment.unsetPermission(permission);

        player.updateCommands();
    }

    /**
     * Checks if a player currently possesses a specific permission node.
     *
     * @param player     The player to check.
     * @param permission The permission node.
     * @return {@code true} if the player has the permission; otherwise {@code false}.
     */
    public boolean has(@NotNull Player player, @NotNull String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Removes all transient permissions and clears the attachment for a player.
     *
     * @param player The player to clear.
     */
    public void clear(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        PermissionAttachment attachment = attachmentMap.remove(uuid);

        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }

    /**
     * Handles automatic cleanup of permission attachments when a player quits.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        clear(event.getPlayer());
    }
}
