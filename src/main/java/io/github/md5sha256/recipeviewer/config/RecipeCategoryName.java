package io.github.md5sha256.recipeviewer.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;

@ConfigSerializable
public record RecipeCategoryName(
        @Setting(value = "category-name") @Nonnull String name
) implements RecipeSetting {

    @Override
    public @NotNull RecipeSettingType settingType() {
        return RecipeSettingType.CATEGORY_NAME;
    }
}
