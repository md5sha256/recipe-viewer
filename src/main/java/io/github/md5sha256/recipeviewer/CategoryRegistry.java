package io.github.md5sha256.recipeviewer;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import io.github.md5sha256.recipeviewer.config.ItemStackConfig;
import io.github.md5sha256.recipeviewer.config.NexoItemStack;
import io.github.md5sha256.recipeviewer.config.RecipeCategoryName;
import io.github.md5sha256.recipeviewer.config.RecipeCategorySetting;
import io.github.md5sha256.recipeviewer.config.RecipeListSetting;
import io.github.md5sha256.recipeviewer.config.RecipeSetting;
import io.github.md5sha256.recipeviewer.config.SimpleItemStack;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeCategoryPointer;
import io.github.md5sha256.recipeviewer.model.RecipeElement;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class CategoryRegistry {

    private final Map<String, RecipeCategory> categories = new HashMap<>();
    private final Logger logger;

    public CategoryRegistry(@Nonnull Logger logger) {
        this.logger = logger;
    }

    @Nonnull
    private static RecipeElement toRecipeElement(@Nonnull RecipeSetting setting) {
        if (setting instanceof RecipeCategoryName(String name)) {
            return new RecipeCategoryPointer(name);
        } else if (setting instanceof RecipeListSetting(
                List<org.bukkit.inventory.Recipe> recipes
        )) {
            return new RecipeList(recipes);
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

    private RecipeCategory parseCategory(@Nonnull ItemStack unknownIcon,
                                         @Nonnull RecipeCategorySetting setting) {
        ItemStackConfig icon = setting.icon();
        ItemStack iconStack;
        if (icon instanceof NexoItemStack(String itemId)) {
            ItemBuilder builder = NexoItems.itemFromId(itemId);
            if (builder == null) {
                this.logger.warning("Unknown nexo item: " + itemId);
                iconStack = unknownIcon;
            } else {
                ItemStack finalIcon = builder.getFinalItemStack();
                iconStack = finalIcon == null ? unknownIcon : finalIcon;
            }
        } else if (icon instanceof SimpleItemStack simpleItemStack) {
            iconStack = simpleItemStack.asItemStack();
        } else {
            iconStack = unknownIcon;
        }
        return new RecipeCategory(
                setting.name(),
                setting.displayName(),
                iconStack.clone(),
                setting.elements().stream().map(CategoryRegistry::toRecipeElement).toList()
        );
    }

}
