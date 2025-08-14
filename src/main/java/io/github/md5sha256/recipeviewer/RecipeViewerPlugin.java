package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.command.RecipeParser;
import io.github.md5sha256.recipeviewer.command.RecipeViewCommand;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

public final class RecipeViewerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        registerCommands();
    }

    private void registerCommands() {
        PaperCommandManager<Source> manager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);
        manager.brigadierManager()
                .registerMapping(new TypeToken<RecipeParser<Source>>() {
                                 },
                        x -> x.toConstant(ArgumentTypes.namespacedKey()).cloudSuggestions());
        manager.command(new RecipeViewCommand(getServer()));
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
