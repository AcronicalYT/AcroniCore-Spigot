package uk.acronical.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullBuilder extends ItemBuilder {

    /**
     * Creates a new SkullBuilder for a player head.
     */
    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     * Sets the owner of the skull by UUID.
     * @param uuid The UUID of the player to set as the owner.
     * @return The current SkullBuilder instance.
     */
    public SkullBuilder owner(UUID uuid) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }
        return this;
    }

}
