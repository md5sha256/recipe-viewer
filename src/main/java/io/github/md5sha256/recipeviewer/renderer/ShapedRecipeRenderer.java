package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

public class ShapedRecipeRenderer implements RecipeRenderer<ShapedRecipe> {

    @Override
    public InventoryHolder renderRecipe(
            @Nonnull Server server,
            @Nonnull ShapedRecipe recipe
    ) {
        Function<InventoryHolder, Inventory> func = holder -> {
            CraftingInventory inventory
                    = (CraftingInventory) server.createInventory(holder, InventoryType.CRAFTING);
            String[] shape = recipe.getShape();
            Map<Character, RecipeChoice> choiceMap = recipe.getChoiceMap();
            for (int row = 0; row < shape.length; row++) {
                char[] rowChars = shape[row].toCharArray();
                for (int col = 0; col < rowChars.length; col++) {
                    RecipeChoice choice = choiceMap.get(rowChars[col]);
                    ItemStack item = RecipeChoiceUtil.getItemStacksFromRecipeChoice(choice)
                            .getFirst();
                    inventory.setItem((row * rowChars.length) + col, item);
                }
            }
            return inventory;
        };
        return new RecipeView(func);
    }


}
