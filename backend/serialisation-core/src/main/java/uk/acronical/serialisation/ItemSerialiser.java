package uk.acronical.serialisation;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import uk.acronical.common.LoggerUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * A utility for converting {@link ItemStack} objects to and from Base64 strings.
 * <p>
 * This class utilise Bukkit's native {@link BukkitObjectOutputStream} to preserve
 * all NBT data, including enchantments, lore, and custom metadata.
 *
 * @author Acronical
 * @since 1.0.3
 */
public class ItemSerialiser {

    /**
     * Serialises an array of item stacks into a Base64 encoded string.
     *
     * @param items The array of {@link ItemStack} objects to serialise.
     * @return A Base64 string representation of the items.
     * @throws IllegalStateException If an error occurs during the serialisation process.
     */
    @NotNull
    public static String toBase64(@NotNull ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            LoggerUtils.severe("An error occurred whilst serialising ItemStacks to Base64.");
            throw new IllegalStateException("Unable to save ItemStacks", e);
        }
    }

    /**
     * Serialises a single item stack into a Base64 encoded string.
     *
     * @param item The {@link ItemStack} to serialise.
     * @return A Base64 string representation of the item.
     */
    @NotNull
    public static String toBase64(@NotNull ItemStack item) {
        return toBase64(new ItemStack[]{item});
    }

    /**
     * Deserialises an array of item stacks from a Base64 encoded string.
     *
     * @param data The Base64 string to decode.
     * @return An array of {@link ItemStack} objects.
     * @throws IllegalStateException If the data is malformed or cannot be read.
     */
    @NotNull
    public static ItemStack[] fromBase64(@NotNull String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            LoggerUtils.severe("An error occurred whilst deserialising ItemStacks from Base64.");
            throw new IllegalStateException("Unable to load ItemStacks", e);
        }
    }

    /**
     * Deserialises a single item stack from a Base64 encoded string.
     *
     * @param data The Base64 string to decode.
     * @return The resulting {@link ItemStack}, or {@code null} if the data was empty.
     */
    @Nullable
    public static ItemStack singleFromBase64(@NotNull String data) {
        ItemStack[] items = fromBase64(data);
        return (items.length > 0) ? items[0] : null;
    }
}
