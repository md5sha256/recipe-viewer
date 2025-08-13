package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.Server;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nonnull;

public interface RecipeRenderer<T extends Recipe> {

    @Nonnull
    InventoryHolder renderRecipe(@Nonnull Server server, @Nonnull T recipe);

}
