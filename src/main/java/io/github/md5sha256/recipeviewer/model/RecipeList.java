package io.github.md5sha256.recipeviewer.model;

import org.bukkit.inventory.Recipe;

import javax.annotation.Nonnull;
import java.util.List;

public record RecipeList(@Nonnull List<Recipe> recipes) implements RecipeElement {
}
