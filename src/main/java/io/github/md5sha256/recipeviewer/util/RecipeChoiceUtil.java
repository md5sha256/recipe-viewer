package io.github.md5sha256.recipeviewer.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nonnull;

public class RecipeChoiceUtil {

    public static @Nonnull ItemStack getDisplayItem(@Nonnull RecipeChoice choice) {
        if (choice == RecipeChoice.empty()) {
            return ItemStack.empty();
        } else if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            // MaterialChoice#getItemStack sets an invalid durability when there are several materials
            return ItemStack.of(materialChoice.getChoices().getFirst());
        }
        // Exact choices return their first item and predicate choices their example item
        return choice.getItemStack();
    }

}
