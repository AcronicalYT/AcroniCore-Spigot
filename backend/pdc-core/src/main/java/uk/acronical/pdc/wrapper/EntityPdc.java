package uk.acronical.pdc.wrapper;

import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import uk.acronical.pdc.PdcService;

/**
 * A specialised wrapper for managing persistent data on {@link Entity} instances.
 * <p>
 * Unlike item stacks, modifications to an entity's persistent data are
 * applied directly to the underlying object without requiring an explicit save call.
 *
 * @author Acronical
 * @since 1.0.1
 */
public class EntityPdc extends PdcWrapper<Entity> {

    /**
     * Initialises a new {@link EntityPdc} wrapper for the specified entity.
     *
     * @param pdcService The service used for {@link org.bukkit.NamespacedKey} generation.
     * @param entity     The {@link Entity} to manage.
     */
    public EntityPdc(@NotNull PdcService pdcService, @NotNull Entity entity) {
        super(pdcService, entity);
    }

    /**
     * Retrieves the {@link PersistentDataContainer} directly from the entity.
     *
     * @return The underlying data container.
     */
    @Override
    @NotNull
    protected PersistentDataContainer getContainer() {
        return holder.getPersistentDataContainer();
    }

    /**
     * Performs no action, as changes to an entity's container are persistent by default.
     * <p>
     * This implementation satisfies the {@link PdcWrapper} contract while
     * acknowledging that entities do not require a meta-reapplication step.
     */
    @Override
    protected void save() {
        // No explicit save needed for entities, as changes to the PersistentDataContainer are automatically saved.
    }
}
