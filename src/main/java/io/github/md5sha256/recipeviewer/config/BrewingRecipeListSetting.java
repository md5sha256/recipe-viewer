package io.github.md5sha256.recipeviewer.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public record BrewingRecipeListSetting(
        @Nonnull List<BrewingRecipeConfig> brewingRecipes
) implements RecipeSetting {

    public BrewingRecipeListSetting {
        brewingRecipes = brewingRecipes == null ? List.of() : List.copyOf(brewingRecipes);
    }

    @Override
    public @NotNull RecipeSettingType settingType() {
        return RecipeSettingType.BREWING_RECIPE_LIST;
    }
}
