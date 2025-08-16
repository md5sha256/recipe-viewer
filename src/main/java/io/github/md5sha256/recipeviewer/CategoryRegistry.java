package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.model.RecipeCategory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryRegistry {

    private final Map<String, RecipeCategory> categories = new HashMap<>();

    public Optional<RecipeCategory> getByName(@Nonnull String name) {
        return Optional.ofNullable(this.categories.get(name));
    }

    public @Nonnull List<String> categoryNames() {
        return List.copyOf(this.categories.keySet());
    }

    public void registerCategory(@Nonnull RecipeCategory category) {
        this.categories.put(category.name(), category);
    }

}
