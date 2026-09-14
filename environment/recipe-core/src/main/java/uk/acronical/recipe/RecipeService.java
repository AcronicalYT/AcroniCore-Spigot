package uk.acronical.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.acronical.common.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A service for dynamically parsing and registering custom crafting recipes.
 * <p>
 * This service utilises an external item resolver to support custom framework
 * items seamlessly alongside vanilla materials.
 *
 * @author Acronical
 * @since 1.0.6
 */
public class RecipeService {

    private final Plugin plugin;
    private final Function<String, ItemStack> itemResolver;
    private final List<NamespacedKey> activeRecipes = new ArrayList<>();

    /**
     * Initialises the recipe service.
     *
     * @param plugin       The plugin instance used for namespaced keys.
     * @param itemResolver A function to resolve string IDs into actual ItemStacks.
     */
    public RecipeService(@NotNull Plugin plugin, @NotNull Function<String, ItemStack> itemResolver) {
        this.plugin = plugin;
        this.itemResolver = itemResolver;
    }

    /**
     * Parses and loads all recipes from a given configuration section.
     *
     * @param recipesSection The YAML configuration section containing recipes.
     */
    public void loadRecipes(@Nullable ConfigurationSection recipesSection) {
        if (recipesSection == null) return;
        unregisterRecipes();

        for (String recipeKey : recipesSection.getKeys(false)) {
            ConfigurationSection config = recipesSection.getConfigurationSection(recipeKey);
            if (config == null) continue;

            NamespacedKey key = new NamespacedKey(plugin, recipeKey.toLowerCase());
            String type = config.getString("type", "shaped").toLowerCase();

            ConfigurationSection ingredients = config.getConfigurationSection("ingredients");
            ConfigurationSection result = config.getConfigurationSection("result");

            if (ingredients == null || result == null) {
                LoggerUtils.warn("Invalid recipe configuration for '" + recipeKey + "'. Missing ingredient or result.");
                continue;
            }

            ItemStack resultItem = parseItem(result.getString("item-id"), result.getInt("count", 1));
            if (resultItem == null) {
                LoggerUtils.warn("Could not resolve result item for recipe '" + recipeKey + "'.");
                continue;
            }

            if (type.equalsIgnoreCase("shaped")) loadShaped(key, resultItem, config, ingredients);
            else if (type.equalsIgnoreCase("shapeless")) loadShapeless(key, resultItem, ingredients);
            else if (type.equalsIgnoreCase("furnace") || type.equals("blasting") || type.equals("smoking") || type.equals("campfire")) loadCooking(key, type, resultItem, config);
            else LoggerUtils.warn("Unknown recipe type '" + type + "' for '" + recipeKey + "'.");
        }
    }

    private void loadShaped(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull ConfigurationSection config, @NotNull ConfigurationSection ingredients) {
        List<String> pattern = config.getStringList("pattern");
        if (pattern.isEmpty() || pattern.size() > 3) {
            LoggerUtils.warn("Invalid shaped pattern for '" + key.getKey() + "'.");
            return;
        }

        for (String row : pattern) {
            if (row.length() > 3) {
                LoggerUtils.warn("Pattern row exceeds 3 characters for '" + key.getKey() + "'.");
                return;
            }
        }

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(pattern.toArray(new String[0]));

        for (String charKey : ingredients.getKeys(false)) {
            ItemStack ingredient = parseItem(ingredients.getString(charKey), 1);
            if (ingredient != null) recipe.setIngredient(charKey.charAt(0), new RecipeChoice.ExactChoice(ingredient));
        }

        if (Bukkit.addRecipe(recipe)) activeRecipes.add(key);
    }

    private void loadShapeless(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull ConfigurationSection ingredients) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        for (String charKey: ingredients.getKeys(false)) {
            int amount = ingredients.getInt(charKey + "-amount", 1);
            String itemId = ingredients.getString(charKey);

            ItemStack ingredient = parseItem(itemId, 1);
            if (ingredient != null) for (int i = 0; i < amount; i++) recipe.addIngredient(new RecipeChoice.ExactChoice(ingredient));
        }

        if (Bukkit.addRecipe(recipe)) activeRecipes.add(key);
    }

    private void loadCooking(@NotNull NamespacedKey key, @NotNull String type, @NotNull ItemStack result, @NotNull ConfigurationSection config) {
        String inputId = config.getString("input");
        if (inputId == null) {
            LoggerUtils.warn("Missing 'input' for cooking recipe '" + key.getKey() + "'.");
            return;
        }

        ItemStack inputItem = parseItem(inputId, 1);
        if (inputItem == null) {
            LoggerUtils.warn("Could not resolve input item for recipe '" + key.getKey() + "'.");
            return;
        }

        RecipeChoice.ExactChoice choice = new RecipeChoice.ExactChoice(inputItem);

        float experience = (float) config.getDouble("experience", 0.1);
        int cookingTime = config.getInt("cooking-time", 200);

        Recipe recipe = switch (type) {
            case "blasting" -> new BlastingRecipe(key, result, choice, experience, cookingTime);
            case "smoking" -> new SmokingRecipe(key, result, choice, experience, cookingTime);
            case "campfire" -> new CampfireRecipe(key, result, choice, experience, cookingTime);
            default -> new FurnaceRecipe(key, result, choice, experience, cookingTime);
        };

        if (Bukkit.addRecipe(recipe)) activeRecipes.add(key);
    }

    @Nullable
    private ItemStack parseItem(@Nullable String id, int amount) {
        if (id == null) return null;

        Material material = Material.matchMaterial(id);
        ItemStack item;

        if (material != null) item = new ItemStack(material);
        else item = itemResolver.apply(id);

        if (item != null) item.setAmount(amount);

        return item;
    }

    /**
     * Unregisters all active recipes created by this service from the server.
     */
    public void unregisterRecipes() {
        for (NamespacedKey key : activeRecipes) Bukkit.removeRecipe(key);
        activeRecipes.clear();
    }

    /**
     * Retrieves an unmodifiable view of all actively registered recipe keys.
     *
     * @return A list of namespaced keys.
     */
    @NotNull
    public List<NamespacedKey> getActiveRecipes() {
        return activeRecipes;
    }
}
