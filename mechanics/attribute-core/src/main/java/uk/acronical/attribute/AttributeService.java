package uk.acronical.attribute;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.acronical.common.LoggerUtils;

/**
 * A service for managing entity attributes and modifiers using NamespacedKeys.
 * <p>
 * This service simplifies the application and removal of modifiers, ensuring
 * that plugin-specific changes are tracked and can be cleared without affecting
 * vanilla or other plugins' modifiers.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class AttributeService {

    private final Plugin plugin;

    /**
     * Initialises the AttributeService.
     *
     * @param plugin The plugin instance used for {@link NamespacedKey} generation.
     */
    public AttributeService(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Applies an attribute modifier to an entity with a specific equipment slot constraint.
     *
     * @param entity             The entity to modify.
     * @param attribute          The {@link Attribute} to target.
     * @param modifierId         The unique identifier for the key.
     * @param amount             The value to apply.
     * @param operation          The {@link AttributeModifier.Operation} type.
     * @param equipmentSlotGroup The {@link EquipmentSlotGroup} this applies to.
     */
    public void applyModifier(@NotNull LivingEntity entity, @NotNull Attribute attribute, @NotNull String modifierId, double amount, @NotNull AttributeModifier.Operation operation, @NotNull EquipmentSlotGroup equipmentSlotGroup) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance == null) {
            LoggerUtils.warn("Attempted to modify unsupported attribute " + attribute + " on " + entity.getName());
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, modifierId.toLowerCase());

        removeModifier(entity, attribute, modifierId);

        AttributeModifier attributeModifier = new AttributeModifier(key, amount, operation, equipmentSlotGroup);
        attributeInstance.addModifier(attributeModifier);
    }

    /**
     * Applies an attribute modifier to an entity.
     * Defaults to {@link EquipmentSlotGroup#ANY}.
     */
    public void applyModifier(@NotNull LivingEntity entity, @NotNull Attribute attribute, @NotNull String modifierId, double amount, @NotNull AttributeModifier.Operation operation) {
        applyModifier(entity, attribute, modifierId, amount, operation, EquipmentSlotGroup.ANY);
    }

    /**
     * Removes a specific modifier from an entity's attribute based on its key.
     *
     * @param entity     The entity to modify.
     * @param attribute  The {@link Attribute} to remove.
     * @param modifierId The unique identifier for the key.
     */
    public void removeModifier(@NotNull LivingEntity entity, @NotNull Attribute attribute, @NotNull String modifierId) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance == null) return;

        NamespacedKey key = new NamespacedKey(plugin, modifierId.toLowerCase());

        for (AttributeModifier existingModifier : attributeInstance.getModifiers()) {
            if (existingModifier.getKey().equals(key)) {
                attributeInstance.removeModifier(existingModifier);
                break;
            }
        }
    }

    /**
     * Adds extra max health to an entity.
     *
     * @param entity      The entity to modify.
     * @param modifierId  The unique identifier for the key.
     * @param extraHearts The number of hearts (1 heart = 2.0 health points).
     */
    public void addMaxHealth(@NotNull LivingEntity entity, @NotNull String modifierId, double extraHearts) {
        applyModifier(entity, Attribute.GENERIC_MAX_HEALTH, modifierId, extraHearts * 2.0, AttributeModifier.Operation.ADD_NUMBER);
    }

    /**
     * Modifies movement speed by a percentage.
     *
     * @param entity     The entity to modify.
     * @param modifierId The unique identifier for the key.
     * @param percentage The percentage increase/decrease (e.g. 0.2 for 20%).
     */
    public void addSpeedPercentage(@NotNull LivingEntity entity, @NotNull String modifierId, double percentage) {
        applyModifier(entity, Attribute.GENERIC_MOVEMENT_SPEED, modifierId, percentage, AttributeModifier.Operation.ADD_SCALAR);
    }

    /**
     * Clears all modifiers created by this plugin for a specific attribute.
     *
     * @param entity    The entity to modify.
     * @param attribute The {@link Attribute} to target.
     */
    public void clearPluginModifiers(@NotNull LivingEntity entity, @NotNull Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        for (AttributeModifier modifier : instance.getModifiers()) {
            if (modifier.getKey().getNamespace().equals(plugin.getName().toLowerCase())) {
                instance.removeModifier(modifier);
            }
        }
    }
}
