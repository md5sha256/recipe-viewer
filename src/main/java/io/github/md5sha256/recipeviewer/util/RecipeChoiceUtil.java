package io.github.md5sha256.recipeviewer.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nonnull;
import java.util.List;

public class RecipeChoiceUtil {

    public static List<ItemStack> getItemStacksFromRecipeChoice(@Nonnull RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getChoices();
        } else if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getChoices().stream().map(ItemStack::of).toList();
        } else if (choice == RecipeChoice.empty()) {
            return List.of(ItemStack.empty());
        }
        // Other choices (e.g. predicate or item type choices) only expose a representative item
        return List.of(choice.getItemStack());
    }

}
