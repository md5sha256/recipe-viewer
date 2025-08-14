package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public class InventoryListener implements Listener {

    private boolean shouldCancel(@Nullable Inventory inventory) {
        return inventory != null && inventory.getHolder() instanceof RecipeView;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (shouldCancel(event.getClickedInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryItemPickup(InventoryPickupItemEvent event) {
        if (shouldCancel(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (shouldCancel(event.getInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (shouldCancel(event.getInventory())) {
            event.setCancelled(true);
        }
    }

}
