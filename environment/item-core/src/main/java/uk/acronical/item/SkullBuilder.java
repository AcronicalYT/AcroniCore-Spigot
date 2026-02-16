package uk.acronical.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A specialised {@link ItemBuilder} for creating and modifying player skulls.
 * <p>
 * This class provides convenience methods for setting skull owners while
 * maintaining the fluent API of the parent builder.
 *
 * @author Acronical
 * @since 1.0.0
 */
public class SkullBuilder extends ItemBuilder {

    /**
     * Initialises a new {@link SkullBuilder} as a {@link Material#PLAYER_HEAD}.
     */
    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     * Sets the owner of the skull using a {@link UUID}.
     * <p>
     * Note: This uses {@link Bukkit#getOfflinePlayer(UUID)}, which may require
     * a profile lookup if the player has not visited the server recently.
     *
     * @param uuid The {@link UUID} of the player to set as the owner.
     * @return The current {@link SkullBuilder} instance.
     */
    public SkullBuilder owner(@NotNull UUID uuid) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }
        return this;
    }

}
