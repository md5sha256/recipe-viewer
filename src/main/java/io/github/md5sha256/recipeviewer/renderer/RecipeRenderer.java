package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nonnull;

public interface RecipeRenderer<T extends Recipe> {

    void renderRecipe(@Nonnull T recipe, @Nonnull Player player);

}
