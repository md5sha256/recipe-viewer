package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.util.CraftingUtil;
import io.github.md5sha256.recipeviewer.util.RecipeChoiceUtil;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.TransmuteRecipe;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class TransmuteRecipeRenderer implements RecipeRenderer<TransmuteRecipe> {

    @Override
    @Nonnull
    public InventoryHolder renderRecipe(
            @Nonnull Server server,
            @Nonnull TransmuteRecipe recipe
    ) {
        Function<InventoryHolder, Inventory> func = holder -> {
            Inventory inventory = server.createInventory(holder, InventoryType.WORKBENCH);
            ItemStack input = RecipeChoiceUtil.getItemStacksFromRecipeChoice(recipe.getInput())
                    .getFirst();
            ItemStack material = RecipeChoiceUtil.getItemStacksFromRecipeChoice(recipe.getMaterial())
                    .getFirst();

            CraftingUtil.setOutputSlot(inventory, recipe.getResult());
            CraftingUtil.setCraftingSlot(inventory, 0, input);
            CraftingUtil.setCraftingSlot(inventory, 1, material);
            return inventory;
        };
        return new RecipeView(func);
    }


}
