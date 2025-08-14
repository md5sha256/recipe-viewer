package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.util.RecipeChoiceUtil;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BlastingRecipeRenderer implements RecipeRenderer<BlastingRecipe> {

    @Override
    public @NotNull InventoryHolder renderRecipe(@NotNull Server server,
                                                 @NotNull BlastingRecipe recipe) {
        Function<InventoryHolder, Inventory> func = holder -> {
            Inventory inventory = server.createInventory(holder, InventoryType.BLAST_FURNACE);
            ItemStack input = RecipeChoiceUtil.getItemStacksFromRecipeChoice(recipe.getInputChoice())
                    .getFirst();
            inventory.setItem(0, input);
            inventory.setItem(2, recipe.getResult());
            return inventory;
        };
        return new RecipeView(func);
    }
}
