package uk.acronical.pdc.wrapper;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.pdc.PdcService;

/**
 * An abstract base for managing persistent data across different Bukkit objects.
 * <p>
 * This wrapper provides a high-level API for interacting with {@link PersistentDataContainer}
 * without needing to manually specify {@link PersistentDataType} constants for standard types.
 *
 * @author Acronical
 * @since 1.0.1
 * @param <T> The type of the wrapped object (e.g., {@link org.bukkit.inventory.ItemStack}).
 */
public abstract class PdcWrapper<T> {

    protected final PdcService service;
    protected final T holder;

    /**
     * Initialises the base wrapper.
     *
     * @param service The {@link PdcService} used for key generation.
     * @param holder  The object holding the persistent data.
     */
    protected PdcWrapper(@NotNull PdcService service, @NotNull T holder) {
        this.service = service;
        this.holder = holder;
    }

    /**
     * Retrieves the underlying data container from the wrapped object.
     *
     * @return The {@link PersistentDataContainer} instance.
     */
    @NotNull
    protected abstract PersistentDataContainer getContainer();

    /**
     * Finalises and persists any changes made to the container.
     */
    protected abstract void save();

    /**
     * Stores a value in the persistent container.
     *
     * @param key   The string key for the data.
     * @param value The value to store.
     * @param <Z>   The data type.
     * @throws IllegalArgumentException If the value type is not supported.
     */
    public <Z> void set(@NotNull String key, @NotNull Z value) {
        PersistentDataType<Z, Z> type = getType(value.getClass());
        getContainer().set(service.key(key), type, value);
        save();
    }

    /**
     * Retrieves a value from the persistent container.
     *
     * @param key  The string key for the data.
     * @param type The expected class of the data.
     * @param <Z>  The data type.
     * @return The stored value, or {@code null} if not found or type-mismatched.
     */
    @Nullable
    public <Z> Z get(@NotNull String key, @NotNull Class<Z> type) {
        NamespacedKey namespacedKey = service.key(key);
        PersistentDataType<Z, Z> pdcType = getType(type);

        if (!getContainer().has(namespacedKey, pdcType)) return null;
        return getContainer().get(namespacedKey, pdcType);
    }

    /**
     * Retrieves a value from the container or returns a default if missing.
     *
     * @param key          The string key for the data.
     * @param type         The expected class of the data.
     * @param defaultValue The value to return if the key is not present.
     * @param <Z>          The data type.
     * @return The stored value or the provided default.
     */
    @NotNull
    public <Z> Z getOrDefault(@NotNull String key, @NotNull Class<Z> type, @NotNull Z defaultValue) {
        Z value = get(key, type);
        return value != null ? value : defaultValue;
    }

    /**
     * Checks if the container holds a specific key with the given type.
     *
     * @param key  The string key to check.
     * @param type The expected class of the data.
     * @return {@code true} if the key exists and matches the type; otherwise {@code false}.
     */
    public boolean has(@NotNull String key, @NotNull Class<?> type) {
        return getContainer().has(service.key(key), getType(type));
    }

    /**
     * Maps a Java class to its corresponding {@link PersistentDataType}.
     * @param clazz The class to map.
     * @return The associated {@link PersistentDataType}.
     * @throws IllegalArgumentException If the class is not a standard PDC type.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    private <Z> PersistentDataType<Z, Z> getType(@NotNull Class<?> clazz) {
        if (clazz == String.class) return (PersistentDataType<Z, Z>) PersistentDataType.STRING;
        if (clazz == Integer.class) return (PersistentDataType<Z, Z>) PersistentDataType.INTEGER;
        if (clazz == Double.class) return (PersistentDataType<Z, Z>) PersistentDataType.DOUBLE;
        if (clazz == Float.class) return (PersistentDataType<Z, Z>) PersistentDataType.FLOAT;
        if (clazz == Long.class) return (PersistentDataType<Z, Z>) PersistentDataType.LONG;
        if (clazz == Byte.class) return (PersistentDataType<Z, Z>) PersistentDataType.BYTE;
        if (clazz == Short.class) return (PersistentDataType<Z, Z>) PersistentDataType.SHORT;
        throw new IllegalArgumentException("Unsupported PDC Type: " + clazz.getSimpleName());
    }
}
