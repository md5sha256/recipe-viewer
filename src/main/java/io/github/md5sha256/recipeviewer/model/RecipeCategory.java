package io.github.md5sha256.recipeviewer.model;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public record RecipeCategory(
        @Nonnull String name,
        @Nonnull Component displayName,
        @Nonnull ItemStack icon,
        @Nonnull List<RecipeElement> elements) {
}
