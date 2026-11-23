package io.github.md5sha256.recipeviewer.renderer;

import io.github.md5sha256.recipeviewer.util.RecipeChoiceUtil;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SmithingTrimRecipeRenderer implements RecipeRenderer<SmithingTrimRecipe> {

    @Override
    public @NotNull InventoryHolder renderRecipe(@NotNull Server server,
                                                 @NotNull SmithingTrimRecipe recipe) {
        Function<InventoryHolder, Inventory> func = holder -> {
            Inventory inventory = server.createInventory(holder, InventoryType.SMITHING);
            ItemStack template = RecipeChoiceUtil.getDisplayItem(recipe.getTemplate());
            ItemStack base = RecipeChoiceUtil.getDisplayItem(recipe.getBase());
            ItemStack addition = RecipeChoiceUtil.getDisplayItem(recipe.getAddition());
            inventory.setItem(0, template);
            inventory.setItem(1, base);
            inventory.setItem(2, addition);
            inventory.setItem(3, recipe.getResult());
            return inventory;
        };
        return new RecipeView(func);
    }
}
