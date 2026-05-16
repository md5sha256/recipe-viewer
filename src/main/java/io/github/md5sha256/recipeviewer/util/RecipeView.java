package io.github.md5sha256.recipeviewer.util;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class RecipeView implements InventoryHolder {

    private final Inventory inventory;
    @Nullable
    private Gui returnGui;

    public RecipeView(@NotNull Function<InventoryHolder, Inventory> function) {
        this.inventory = function.apply(this);
    }

    public void setReturnGui(@NotNull Gui returnGui) {
        this.returnGui = returnGui;
    }

    public @NotNull Optional<Gui> returnGui() {
        return Optional.ofNullable(this.returnGui);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
