package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class ShapelessRecipeRenderer implements RecipeRenderer<ShapelessRecipe> {

    @Override
    @Nonnull
    public InventoryHolder renderRecipe(@NotNull Server server, @NotNull ShapelessRecipe recipe) {
        List<RecipeChoice> choices = recipe.getChoiceList();

        Function<InventoryHolder, Inventory> func = holder -> {
            Inventory inventory = server.createInventory(holder, InventoryType.WORKBENCH);
            inventory.setItem(0, recipe.getResult());
            for (int row = 0; row < choices.size(); row++) {
                RecipeChoice choice = choices.get(row);
                ItemStack item = RecipeChoiceUtil.getItemStacksFromRecipeChoice(choice).getFirst();
                inventory.setItem(row + 1, item);
            }
            return inventory;
        };
        return new RecipeView(func);
    }
}
