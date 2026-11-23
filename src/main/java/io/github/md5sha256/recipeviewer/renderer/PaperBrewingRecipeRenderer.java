package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.util.RecipeChoiceUtil;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class PaperBrewingRecipeRenderer implements RecipeRenderer<BrewingRecipe> {

    @Override
    public @NotNull InventoryHolder renderRecipe(@NotNull Server server,
                                                 @NotNull BrewingRecipe recipe) {
        Function<InventoryHolder, Inventory> func = holder -> {
            Inventory inventory = server.createInventory(holder, InventoryType.BREWING);
            ItemStack input = RecipeChoiceUtil.getDisplayItem(recipe.getInput());
            ItemStack ingredient = RecipeChoiceUtil.getDisplayItem(recipe.getIngredient());
            // The recipe describes a single bottle, but fill all three slots like a real brewing stand
            for (int i = 0; i < 3; i++) {
                inventory.setItem(i, input);
            }
            inventory.setItem(3, ingredient);
            return inventory;
        };
        return new RecipeView(func);
    }
}
