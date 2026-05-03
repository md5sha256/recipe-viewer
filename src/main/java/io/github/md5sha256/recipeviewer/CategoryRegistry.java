package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.config.BrewingRecipeConfig;
import io.github.md5sha256.recipeviewer.config.BrewingRecipeListSetting;
import io.github.md5sha256.recipeviewer.config.ItemStackConfig;
import io.github.md5sha256.recipeviewer.config.MinecraftItem;
import io.github.md5sha256.recipeviewer.config.NexoItemStack;
import io.github.md5sha256.recipeviewer.config.RecipeCategoryName;
import io.github.md5sha256.recipeviewer.config.RecipeCategorySetting;
import io.github.md5sha256.recipeviewer.config.RecipeListSetting;
import io.github.md5sha256.recipeviewer.config.RecipeSetting;
import io.github.md5sha256.recipeviewer.config.SimpleItemStack;
import io.github.md5sha256.recipeviewer.model.BrewingRecipeList;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeCategoryPointer;
import io.github.md5sha256.recipeviewer.model.RecipeElement;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import io.github.md5sha256.recipeviewer.recipe.CustomBrewingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class CategoryRegistry {

    private final Map<String, RecipeCategory> categories = new HashMap<>();
    private final Logger logger;
    private final NexoFeature nexoFeature;

    public CategoryRegistry(@Nonnull Logger logger, @Nonnull NexoFeature nexoFeature) {
        this.logger = logger;
        this.nexoFeature = nexoFeature;
    }

    @Nonnull
    private RecipeElement toRecipeElement(@Nonnull RecipeSetting setting) {
        if (setting instanceof RecipeCategoryName(String name)) {
            return new RecipeCategoryPointer(name);
        } else if (setting instanceof RecipeListSetting(
                List<org.bukkit.inventory.Recipe> recipes
        )) {
            return new RecipeList(recipes.stream().filter(Objects::nonNull).toList());
        } else if (setting instanceof BrewingRecipeListSetting listSetting) {
            return new BrewingRecipeList(resolveBrewingRecipes(listSetting.brewingRecipes()));
        }
        throw new IllegalArgumentException("Unsupported recipe setting: " + setting.settingType());
    }

    public Optional<RecipeCategory> getByName(@Nonnull String name) {
        return Optional.ofNullable(this.categories.get(name));
    }

    public @Nonnull List<String> categoryNames() {
        return List.copyOf(this.categories.keySet());
    }

    public void registerCategory(@Nonnull RecipeCategory category) {
        this.categories.put(category.name(), category);
    }

    public void clear() {
        this.categories.clear();
    }

    public void loadCategories(@Nonnull List<RecipeCategorySetting> settings) {
        ItemStack unknownIcon = ItemStack.of(Material.BARRIER);
        Map<String, RecipeCategory> categoryMap = new HashMap<>();
        for (RecipeCategorySetting setting : settings) {
            RecipeCategory category = parseCategory(unknownIcon, setting);
            categoryMap.put(category.name(), category);
        }
        this.categories.putAll(categoryMap);
    }

    private RecipeCategory parseCategory(
            @Nonnull ItemStack unknownIcon,
            @Nonnull RecipeCategorySetting setting
    ) {
        ItemStack iconStack = resolveItemStackConfig(setting.icon(), unknownIcon);
        return new RecipeCategory(
                setting.name(),
                setting.displayName(),
                iconStack.clone(),
                setting.elements().stream().map(this::toRecipeElement).toList()
        );
    }

    @Nonnull
    private ItemStack resolveItemStackConfig(
            @Nonnull ItemStackConfig config,
            @Nonnull ItemStack unknownIcon
    ) {
        if (config instanceof NexoItemStack(String itemId)) {
            if (this.nexoFeature.isDisabled()) {
                this.logger.warning("Nexo is disabled! Item: " + itemId);
                return unknownIcon;
            }
            ItemStack resolved = this.nexoFeature.getItemFromId(itemId);
            if (resolved == null) {
                this.logger.warning("Unknown nexo item: " + itemId);
                return unknownIcon;
            }
            return resolved;
        }
        if (config instanceof SimpleItemStack simpleItemStack) {
            return simpleItemStack.asItemStack();
        }
        if (config instanceof MinecraftItem(String input)) {
            try {
                return Bukkit.getItemFactory().createItemStack(input);
            } catch (IllegalArgumentException ex) {
                this.logger.warning("Invalid minecraft item string: " + input + " — " + ex.getMessage());
                return unknownIcon;
            }
        }
        return unknownIcon;
    }

    @Nonnull
    private List<CustomBrewingRecipe> resolveBrewingRecipes(
            @Nonnull List<BrewingRecipeConfig> configs
    ) {
        ItemStack unknownIcon = ItemStack.of(Material.BARRIER);
        List<CustomBrewingRecipe> out = new ArrayList<>();
        for (BrewingRecipeConfig config : configs) {
            try {
                List<ItemStack> inputs = new ArrayList<>();
                for (ItemStackConfig c : config.inputs()) {
                    inputs.add(resolveItemStackConfig(c, unknownIcon).clone());
                }
                ItemStack ingredient = config.ingredient() == null
                        ? null
                        : resolveItemStackConfig(config.ingredient(), unknownIcon).clone();
                List<ItemStack> outputs = new ArrayList<>();
                for (ItemStackConfig c : config.outputs()) {
                    outputs.add(resolveItemStackConfig(c, unknownIcon).clone());
                }
                if (!inputs.isEmpty()) {
                    inputs = padToThreeBottleSlots(inputs);
                }
                if (!outputs.isEmpty()) {
                    outputs = padToThreeBottleSlots(outputs);
                }
                out.add(new CustomBrewingRecipe(
                        inputs,
                        ingredient,
                        outputs,
                        config.consumeIngredientValue()));
            } catch (IllegalArgumentException ex) {
                this.logger.warning("Skipping brewing recipe: " + ex.getMessage());
            }
        }
        return out;
    }

    @Nonnull
    private static List<ItemStack> padToThreeBottleSlots(@Nonnull List<ItemStack> row) {
        if (row.isEmpty()) {
            return List.of();
        }
        List<ItemStack> padded = new ArrayList<>(3);
        for (int i = 0; i < Math.min(3, row.size()); i++) {
            padded.add(row.get(i).clone());
        }
        while (padded.size() < 3) {
            padded.add(padded.get(padded.size() - 1).clone());
        }
        return padded;
    }

}
