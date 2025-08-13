package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class RecipeChoiceUtil {

    public static List<ItemStack> getItemStacksFromRecipeChoice(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getChoices();
        } else if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getChoices().stream().map(ItemStack::of).toList();
        }
        throw new UnsupportedOperationException("Recipe choice class not supported: " + choice.getClass());
    }

}
