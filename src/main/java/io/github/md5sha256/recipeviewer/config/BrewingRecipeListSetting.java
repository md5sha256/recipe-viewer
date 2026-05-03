package io.github.md5sha256.recipeviewer.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import java.util.List;

@ConfigSerializable
public record BrewingRecipeListSetting(
        @Setting("brewing-recipes") @Required @Nonnull List<BrewingRecipeConfig> brewingRecipes
) implements RecipeSetting {

    public BrewingRecipeListSetting {
        brewingRecipes = brewingRecipes == null ? List.of() : List.copyOf(brewingRecipes);
    }

    @Override
    public @NotNull RecipeSettingType settingType() {
        return RecipeSettingType.BREWING_RECIPE_LIST;
    }
}
