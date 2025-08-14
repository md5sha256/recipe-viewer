package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.util.RecipeChoiceUtil;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class StonecuttingRecipeRenderer implements RecipeRenderer<StonecuttingRecipe> {

    @Override
    public @NotNull InventoryHolder renderRecipe(@NotNull Server server,
                                                 @NotNull StonecuttingRecipe recipe) {
        Function<InventoryHolder, Inventory> func = holder -> {
            StonecutterInventory inventory = (StonecutterInventory) server.createInventory(holder, InventoryType.STONECUTTER);
            ItemStack input = RecipeChoiceUtil.getItemStacksFromRecipeChoice(recipe.getInputChoice())
                    .getFirst();

            inventory.setInputItem(input);
            inventory.setResult(recipe.getResult());
            return inventory;
        };
        return new RecipeView(func);
    }
}
