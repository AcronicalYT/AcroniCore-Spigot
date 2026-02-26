package uk.acronical.loot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a single entry within a loot table with a specific probability weight.
 * <p>
 * This model facilitates the generation of randomised item stacks, supporting
 * variable counts and weighted selection logic.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class LootEntry {

    private final ItemStack item;
    private final double weight;
    private final int minCount, maxCount;

    /**
     * Initialises a new loot entry.
     *
     * @param item     The template {@link ItemStack} for this entry.
     * @param weight   The relative probability weight (higher = more common).
     * @param minCount The minimum number of items to generate.
     * @param maxCount The maximum number of items to generate.
     */
    public LootEntry(@NotNull ItemStack item, double weight, int minCount, int maxCount) {
        this.item = item;
        this.weight = Math.max(0, weight);
        this.minCount = Math.max(1, minCount);
        this.maxCount = Math.max(this.minCount, maxCount);
    }

    /**
     * Retrieves the relative weight of this entry.
     *
     * @return The probability weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Generates a randomised {@link ItemStack} based on this entry's parameters.
     * <p>
     * This method clones the template item and assigns a random quantity between
     * the defined minimum and maximum bounds.
     *
     * @return A new item stack instance.
     */
    @NotNull
    public ItemStack generate() {
        ItemStack clone = item.clone();
        if (minCount == maxCount) clone.setAmount(minCount);
        else clone.setAmount(ThreadLocalRandom.current().nextInt(minCount, maxCount + 1));
        return clone;
    }
}
