package io.github.md5sha256.recipeviewer.model;

import io.github.md5sha256.recipeviewer.recipe.CustomBrewingRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public record BrewingRecipeList(@Nonnull List<CustomBrewingRecipe> recipes) implements RecipeElement {
}
