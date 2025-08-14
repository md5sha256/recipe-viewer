package io.github.md5sha256.recipeviewer.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingUtil {

    public static void setCraftingSlot(Inventory inventory, int slot, ItemStack item) {
        inventory.setItem(slot + 1, item);
    }

    public static void setCraftingSlot(Inventory inventory, int row, int col, ItemStack item) {
        setCraftingSlot(inventory, row * 3 + col, item);
    }

    public static void setOutputSlot(Inventory inventory, ItemStack itemStack) {
        inventory.setItem(0, itemStack);
    }

}
