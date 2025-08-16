package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.command.CategoryViewCommand;
import io.github.md5sha256.recipeviewer.command.RecipeParser;
import io.github.md5sha256.recipeviewer.command.RecipeViewCommand;
import io.github.md5sha256.recipeviewer.config.RecipeCategoryName;
import io.github.md5sha256.recipeviewer.config.RecipeCategorySetting;
import io.github.md5sha256.recipeviewer.config.RecipeListSetting;
import io.github.md5sha256.recipeviewer.config.Serializers;
import io.github.md5sha256.recipeviewer.gui.RecipeGUI;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import io.github.md5sha256.recipeviewer.model.SimpleItemStack;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
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
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;


public final class RecipeViewerPlugin extends JavaPlugin {

    private final Renderers renderers = new Renderers();
    private final CategoryRegistry registry = new CategoryRegistry();
    private RecipeGUI gui;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        registerRenderers();
        this.gui = new RecipeGUI(this.renderers, this, this.getServer());
        registerCommands();
        createDataFolder();
        saveDummyData();
    }

    private void createDataFolder() {
        try {
            if (!Files.exists(getDataPath())) {
                Files.createDirectory(getDataPath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to create data folder!");
        }
    }

    private void saveDummyData() {
        File file = new File(getDataFolder(), "dummy-recipes.yml");
        var loader = Serializers.yamlLoader(getServer())
                .file(file)
                .build();
        ConfigurationNode root = loader.createNode();
        try {
            root.set(createDummy());
            loader.save(root);
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to save dummy data!");
        }
    }

    private RecipeCategorySetting createDummy() {
        var spliterator = Spliterators.spliteratorUnknownSize(getServer().recipeIterator(),
                Spliterator.NONNULL);
        List<Recipe> recipes = StreamSupport.stream(spliterator, false)
                .limit(10)
                .toList();
        return new RecipeCategorySetting(
                "dummy",
                Component.text("dummy-category", NamedTextColor.AQUA),
                List.of(
                        new RecipeCategoryName("dummy-category2"),
                        new RecipeListSetting(recipes.subList(0, 5)),
                        new RecipeCategoryName("dummy-category3"),
                        new RecipeListSetting(recipes.subList(5, recipes.size()))
                )
        );
    }

    private void registerDummy() {
        RecipeList list = new RecipeList(getServer().getRecipesFor(ItemStack.of(Material.ACACIA_BUTTON)));
        RecipeCategory subcategory1 = new RecipeCategory(
                "subcategory1",
                Component.text("subcategory1", NamedTextColor.WHITE),
                new SimpleItemStack(Material.BARRIER,
                        Component.text("subcategory1"),
                        List.of()).asItemStack(),
                List.of()
        );
        RecipeCategory subcategory2 = new RecipeCategory(
                "subcategory2",
                Component.text("subcategory2", NamedTextColor.WHITE),
                new SimpleItemStack(Material.BARRIER,
                        Component.text("subcategory2"),
                        List.of()).asItemStack(),
                List.of()
        );
        RecipeCategory defaultCategory = new RecipeCategory(
                "default",
                Component.text("default"),
                new SimpleItemStack(Material.BARRIER,
                        Component.text("default"),
                        List.of()).asItemStack(),
                List.of(subcategory1, subcategory2, list)
        );
        this.registry.registerCategory(subcategory1);
        this.registry.registerCategory(subcategory2);
        this.registry.registerCategory(defaultCategory);
    }

    private void registerRenderers() {
        this.renderers.registerRenderer(ShapedRecipe.class, new ShapedRecipeRenderer())
                .registerRenderer(ShapelessRecipe.class, new ShapelessRecipeRenderer())
                .registerRenderer(FurnaceRecipe.class, new FurnaceRecipeRenderer())
                .registerRenderer(BlastingRecipe.class, new BlastingRecipeRenderer())
                .registerRenderer(SmithingTrimRecipe.class, new SmithingTrimRecipeRenderer())
                .registerRenderer(SmithingTransformRecipe.class,
                        new SmithingTransformRecipeRenderer())
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
        manager.command(new RecipeViewCommand(getServer(), this.renderers))
                .command(new CategoryViewCommand(this.registry, this.gui));
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
