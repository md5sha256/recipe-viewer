package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class ShapelessRecipeRenderer implements RecipeRenderer<ShapelessRecipe> {

    @Override
    public InventoryHolder renderRecipe(@NotNull Server server, @NotNull ShapelessRecipe recipe) {
        List<RecipeChoice> choices = recipe.getChoiceList();

        Function<InventoryHolder, Inventory> func = holder -> {
            CraftingInventory inventory
                    = (CraftingInventory) server.createInventory(holder, InventoryType.CRAFTING);
            for (int row = 0; row < choices.size(); row++) {
                RecipeChoice choice = choices.get(row);
                ItemStack item = RecipeChoiceUtil.getItemStacksFromRecipeChoice(choice).getFirst();
                inventory.setItem(row * 3, item);
            }
            return inventory;
        };
        return new RecipeView(func);
    }
}
