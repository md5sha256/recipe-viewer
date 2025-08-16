package io.github.md5sha256.recipeviewer.config;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import java.util.List;

@ConfigSerializable
public record RecipeListSetting(
        @Nonnull @Setting(value = "recipes") List<Recipe> recipes
) implements RecipeSetting {

    @Override
    public @NotNull RecipeSettingType settingType() {
        return RecipeSettingType.RECIPE_LIST;
    }
}
