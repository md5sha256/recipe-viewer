package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.command.RecipeParser;
import io.github.md5sha256.recipeviewer.command.RecipeViewCommand;
import io.github.md5sha256.recipeviewer.renderer.BlastingRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.FurnaceRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.Renderers;
import io.github.md5sha256.recipeviewer.renderer.ShapedRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.ShapelessRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.SmithingTransformRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.SmithingTrimRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.SmokingRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.StonecuttingRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.TransmuteRecipeRenderer;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Keyed;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;


public final class RecipeViewerPlugin extends JavaPlugin {

    private final Renderers renderers = new Renderers();


    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        registerRenderers();
        registerCommands();
    }

    private void registerRenderers() {
        this.renderers.registerRenderer(ShapedRecipe.class, new ShapedRecipeRenderer())
                .registerRenderer(ShapelessRecipe.class, new ShapelessRecipeRenderer())
                .registerRenderer(FurnaceRecipe.class, new FurnaceRecipeRenderer())
                .registerRenderer(BlastingRecipe.class, new BlastingRecipeRenderer())
                .registerRenderer(SmithingTrimRecipe.class, new SmithingTrimRecipeRenderer())
                .registerRenderer(SmithingTransformRecipe.class, new SmithingTransformRecipeRenderer())
                .registerRenderer(SmokingRecipe.class, new SmokingRecipeRenderer())
                .registerRenderer(StonecuttingRecipe.class, new StonecuttingRecipeRenderer())
                .registerRenderer(TransmuteRecipe.class, new TransmuteRecipeRenderer());
    }

    private void registerCommands() {
        PaperCommandManager<Source> manager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);
        manager.brigadierManager()
                .registerMapping(new TypeToken<RecipeParser<Source>>() {
                                 },
                        x -> x.toConstant(ArgumentTypes.namespacedKey()).cloudSuggestions());
        manager.command(new RecipeViewCommand(getServer(), this.renderers));
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
