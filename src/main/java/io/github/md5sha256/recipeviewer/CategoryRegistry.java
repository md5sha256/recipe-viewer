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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class CategoryRegistry {

    private final Map<String, RecipeCategory> categories = new HashMap<>();
    private final Logger logger;
    private final NexoFeature nexoFeature;
    private final Server server;

    public CategoryRegistry(
            @Nonnull Logger logger,
            @Nonnull NexoFeature nexoFeature,
            @Nonnull Server server
    ) {
        this.logger = logger;
        this.nexoFeature = nexoFeature;
        this.server = server;
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

    public @Nonnull List<CustomBrewingRecipe> findBrewingRecipes(@Nonnull String query) {
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        List<CustomBrewingRecipe> results = new ArrayList<>();
        Set<CustomBrewingRecipe> seen = new HashSet<>();
        for (RecipeCategory category : this.categories.values()) {
            collectBrewingRecipesMatching(category.elements(), lowerQuery, results, seen);
        }
        return results;
    }

    public @Nonnull List<CustomBrewingRecipe> findBrewingRecipes(@Nonnull ItemStack item) {
        if (item.isEmpty()) {
            return List.of();
        }
        List<CustomBrewingRecipe> results = new ArrayList<>();
        Set<CustomBrewingRecipe> seen = new HashSet<>();
        for (RecipeCategory category : this.categories.values()) {
            collectBrewingRecipesForItem(category.elements(), item, results, seen);
        }
        return results;
    }

    private void collectBrewingRecipesMatching(
            @Nonnull List<RecipeElement> elements,
            @Nonnull String lowerQuery,
            @Nonnull List<CustomBrewingRecipe> results,
            @Nonnull Set<CustomBrewingRecipe> seen
    ) {
        for (RecipeElement element : elements) {
            if (element instanceof BrewingRecipeList(List<CustomBrewingRecipe> recipes)) {
                for (CustomBrewingRecipe recipe : recipes) {
                    if (seen.add(recipe) && brewingRecipeMatchesQuery(recipe, lowerQuery)) {
                        results.add(recipe);
                    }
                }
            } else if (element instanceof RecipeCategoryPointer(String name)) {
                getByName(name).ifPresent(category ->
                        collectBrewingRecipesMatching(category.elements(), lowerQuery, results, seen));
            }
        }
    }

    private void collectBrewingRecipesForItem(
            @Nonnull List<RecipeElement> elements,
            @Nonnull ItemStack item,
            @Nonnull List<CustomBrewingRecipe> results,
            @Nonnull Set<CustomBrewingRecipe> seen
    ) {
        for (RecipeElement element : elements) {
            if (element instanceof BrewingRecipeList(List<CustomBrewingRecipe> recipes)) {
                for (CustomBrewingRecipe recipe : recipes) {
                    if (seen.add(recipe) && brewingRecipeUsesItem(recipe, item)) {
                        results.add(recipe);
                    }
                }
            } else if (element instanceof RecipeCategoryPointer(String name)) {
                getByName(name).ifPresent(category ->
                        collectBrewingRecipesForItem(category.elements(), item, results, seen));
            }
        }
    }

    private boolean brewingRecipeMatchesQuery(@Nonnull CustomBrewingRecipe recipe, @Nonnull String lowerQuery) {
        for (ItemStack input : recipe.inputs()) {
            if (itemMatchesQuery(input, lowerQuery)) {
                return true;
            }
        }
        if (recipe.ingredient() != null && itemMatchesQuery(recipe.ingredient(), lowerQuery)) {
            return true;
        }
        for (ItemStack output : recipe.outputs()) {
            if (itemMatchesQuery(output, lowerQuery)) {
                return true;
            }
        }
        return false;
    }

    private boolean brewingRecipeUsesItem(@Nonnull CustomBrewingRecipe recipe, @Nonnull ItemStack item) {
        for (ItemStack input : recipe.inputs()) {
            if (itemsSimilar(item, input)) {
                return true;
            }
        }
        if (recipe.ingredient() != null && itemsSimilar(item, recipe.ingredient())) {
            return true;
        }
        for (ItemStack output : recipe.outputs()) {
            if (itemsSimilar(item, output)) {
                return true;
            }
        }
        return false;
    }

    private boolean itemMatchesQuery(@Nonnull ItemStack stack, @Nonnull String lowerQuery) {
        if (stack.isEmpty()) {
            return false;
        }
        String nexoId = this.nexoFeature.getIdFromItem(stack);
        if (nexoId != null && nexoId.toLowerCase(Locale.ROOT).contains(lowerQuery)) {
            return true;
        }
        if (stack.getType().getKey().value().contains(lowerQuery)
                || stack.getType().name().toLowerCase(Locale.ROOT).contains(lowerQuery)) {
            return true;
        }
        String displayName = PlainTextComponentSerializer.plainText()
                .serialize(this.server.getItemFactory().displayName(stack))
                .toLowerCase(Locale.ROOT);
        return displayName.contains(lowerQuery);
    }

    private boolean itemsSimilar(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        String idA = this.nexoFeature.getIdFromItem(a);
        String idB = this.nexoFeature.getIdFromItem(b);
        if (idA != null || idB != null) {
            return idA != null && idA.equals(idB);
        }
        return a.isSimilar(b);
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
                return this.server.getItemFactory().createItemStack(input);
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
                        config.consumeIngredient()));
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
