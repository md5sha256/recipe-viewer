package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RecipeView implements InventoryHolder {

    private final Inventory inventory;

    public RecipeView(Function<InventoryHolder, Inventory> function) {
        this.inventory = function.apply(this);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
