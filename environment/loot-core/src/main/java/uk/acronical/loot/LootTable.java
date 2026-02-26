package uk.acronical.loot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of {@link LootEntry} objects used to determine random item drops.
 * <p>
 * This class utilise a cumulative weight algorithm to select entries,
 * supporting multiple rolls and a "no-drop" chance via empty weights.
 *
 * @author Acronical
 * @since 1.0.5
 */
public class LootTable {

    private final List<LootEntry> entries = new ArrayList<>();
    private double totalWeight = 0.0;

    /**
     * Adds an entry to the loot table and updates the total probability pool.
     *
     * @param entry The {@link LootEntry} to include.
     * @return The current {@link LootTable} instance for chaining.
     */
    public LootTable addEntry(@NotNull LootEntry entry) {
        entries.add(entry);
        totalWeight += entry.getWeight();
        return this;
    }

    /**
     * Increases the total weight without adding an item, effectively
     * initialising or increasing the chance of receiving no items (a "null" roll).
     *
     * @param weight The amount of "empty" probability to add.
     * @return The current {@link LootTable} instance.
     */
    public LootTable addEmptyWeight(double weight) {
        totalWeight += Math.max(0, weight);
        return this;
    }

    /**
     * Performs multiple rolls against the loot table.
     *
     * @param rolls The number of times to roll the table.
     * @return A list of generated {@link ItemStack} objects.
     */
    @NotNull
    public List<ItemStack> roll(int rolls) {
        List<ItemStack> drops = new ArrayList<>();

        if (entries.isEmpty() || totalWeight <= 0) return drops;

        for (int i = 0; i < rolls; i++) {
            ItemStack drop = rollSingle();
            if (drop != null) drops.add(drop);
        }

        return drops;
    }

    /**
     * Selects a single item based on the cumulative weight algorithm.
     *
     * @return A generated {@link ItemStack}, or {@code null} if an empty weight was rolled.
     */
    @Nullable
    private ItemStack rollSingle() {
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentWeight = 0.0;

        for (LootEntry lootEntry : entries) {
            currentWeight += lootEntry.getWeight();
            if (random <= currentWeight) return lootEntry.generate();
        }

        return null;
    }
}
