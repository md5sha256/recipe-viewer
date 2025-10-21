package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.recipe.CustomBrewingRecipe;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class BrewingRecipeRenderer implements RecipeRenderer<CustomBrewingRecipe> {

    @Override
    public @NotNull InventoryHolder renderRecipe(@NotNull Server server,
                                                 @NotNull CustomBrewingRecipe recipe) {
        Function<InventoryHolder, Inventory> func = holder -> {
            List<ItemStack> inputs = recipe.inputs();
            List<ItemStack> outputs = recipe.outputs();
            ItemStack ingredient = recipe.ingredient();
            Inventory previous = server.createInventory(holder, InventoryType.BREWING);
            for (int i = 0; i < inputs.size(); i++) {
                previous.setItem(i, inputs.get(i));
            }
            previous.setItem(3, ingredient);
            Inventory completed = server.createInventory(holder, InventoryType.BREWING);
            if (!recipe.consumeIngredient()) {
                completed.setItem(3, ingredient);
            }
            for (int i = 0; i < inputs.size(); i++) {
                completed.setItem(i, outputs.get(i));
            }
            return previous;
        };
        return new RecipeView(func);
    }
}
