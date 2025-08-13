package io.github.md5sha256.recipeviewer;

import org.bukkit.Keyed;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeViewerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getScheduler().runTaskLater(this, this::printRecipes, 10);
    }

    private void printRecipes() {
        getLogger().info("Printing recipes...");
        var iterator = getServer().recipeIterator();
        int nexoRecipeCount = 0;
        String key = "none";
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (!(recipe instanceof Keyed keyed)) {
                getLogger().info("Recipe is not keyed!");
                continue;
            }
            if (!keyed.getKey().getNamespace().equals("minecraft")) {
                nexoRecipeCount += 1;
                key = keyed.getKey().toString();
            }
        }
        getLogger().info("Found " + nexoRecipeCount + " Nexo recipes");
        getLogger().info(key);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
