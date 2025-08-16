package io.github.md5sha256.recipeviewer;

import io.github.md5sha256.recipeviewer.command.CategoryViewCommand;
import io.github.md5sha256.recipeviewer.command.CustomCommandBean;
import io.github.md5sha256.recipeviewer.command.RecipeParser;
import io.github.md5sha256.recipeviewer.command.RecipeViewCommand;
import io.github.md5sha256.recipeviewer.command.ReloadCommand;
import io.github.md5sha256.recipeviewer.config.NexoItemStack;
import io.github.md5sha256.recipeviewer.config.RecipeCategoryName;
import io.github.md5sha256.recipeviewer.config.RecipeCategorySetting;
import io.github.md5sha256.recipeviewer.config.RecipeListSetting;
import io.github.md5sha256.recipeviewer.config.Serializers;
import io.github.md5sha256.recipeviewer.config.SimpleItemStack;
import io.github.md5sha256.recipeviewer.gui.RecipeGUI;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeCategoryPointer;
import io.github.md5sha256.recipeviewer.model.RecipeList;
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
import org.bukkit.scheduler.BukkitScheduler;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.ConfigurationNode;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public final class RecipeViewerPlugin extends JavaPlugin {

    private final Renderers renderers = new Renderers();
    private final CategoryRegistry registry = new CategoryRegistry(getLogger());
    private RecipeGUI gui;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        registerRenderers();
        this.gui = new RecipeGUI(this.renderers, this.registry, this, this.getServer());
        registerCommands();
        createDataFolder();
        saveDummyData1();
        saveDummyData2();
        reloadRegistry();
    }

    public CompletableFuture<Void> reloadRegistry() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskAsynchronously(this, () -> {
            Path categoriesDir = getDataPath().resolve("categories");
            List<RecipeCategorySetting> settings = new ArrayList<>();
            try (Stream<Path> stream = Files.walk(categoriesDir, 1)) {
                stream.forEach(path -> loadCategoryFromFile(path).ifPresent(settings::add));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            scheduler.runTask(this, () -> {
                this.registry.clear();
                this.registry.loadCategories(settings);
                future.complete(null);
            });
        });
        return future;
    }

    private Optional<RecipeCategorySetting> loadCategoryFromFile(@Nonnull Path path) {
        try {
            if (!Files.isRegularFile(path)) {
                return Optional.empty();
            }
            ConfigurationNode node = Serializers.yamlLoader(getServer())
                    .file(path.toFile())
                    .build()
                    .load();

            return Optional.ofNullable(node.get(RecipeCategorySetting.class));
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to load recipe category from file: " + path.getFileName());
        }
        return Optional.empty();
    }

    private void createDataFolder() {
        try {
            if (!Files.exists(getDataPath())) {
                Files.createDirectory(getDataPath());
            }
            Path categoriesDir = getDataPath().resolve("categories");
            if (!Files.exists(categoriesDir)) {
                Files.createDirectory(categoriesDir);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to create data folder!");
        }
    }

    private void saveDummyData1() {
        File file = new File(getDataFolder(), "dummy-category1.yml");
        var loader = Serializers.yamlLoader(getServer())
                .file(file)
                .build();
        ConfigurationNode root = loader.createNode();
        try {
            root.set(createDummy1());
            loader.save(root);
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to save dummy data!");
        }
    }

    private void saveDummyData2() {
        File file = new File(getDataFolder(), "dummy-category2.yml");
        var loader = Serializers.yamlLoader(getServer())
                .file(file)
                .build();
        ConfigurationNode root = loader.createNode();
        try {
            root.set(createDummy2());
            loader.save(root);
        } catch (IOException ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to save dummy data!");
        }
    }

    private RecipeCategorySetting createDummy1() {
        var spliterator = Spliterators.spliteratorUnknownSize(getServer().recipeIterator(),
                Spliterator.NONNULL);
        List<Recipe> recipes = StreamSupport.stream(spliterator, false)
                .limit(100)
                .toList();
        return new RecipeCategorySetting(
                "dummy-category1",
                Component.text("dummy-category", NamedTextColor.AQUA),
                new SimpleItemStack(Material.BARRIER,
                        Component.text("dummy-category1"),
                        List.of(Component.text("lore!"))),
                List.of(
                        new RecipeCategoryName("dummy-category1"),
                        new RecipeListSetting(recipes.subList(0, 5)),
                        new RecipeCategoryName("dummy-category2"),
                        new RecipeListSetting(recipes.subList(5, recipes.size()))
                )
        );
    }

    private RecipeCategorySetting createDummy2() {
        var spliterator = Spliterators.spliteratorUnknownSize(getServer().recipeIterator(),
                Spliterator.NONNULL);
        List<Recipe> recipes = StreamSupport.stream(spliterator, false)
                .limit(100)
                .toList();
        return new RecipeCategorySetting(
                "dummy-category2",
                Component.text("dummy-category2", NamedTextColor.YELLOW),
                new NexoItemStack("nexo-item-id"),
                List.of(
                        new RecipeCategoryName("dummy-category1"),
                        new RecipeListSetting(recipes.subList(0, 5)),
                        new RecipeCategoryName("dummy-category2"),
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
                List.of(new RecipeCategoryPointer("subcategory-1"),
                        new RecipeCategoryPointer("subcategory-2"),
                        list)
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
        var rootCommand = manager.commandBuilder("recipeviewer", "rv").permission("recipeviewer.base");
        List<CustomCommandBean<Source>> beans = List.of(
                new RecipeViewCommand(getServer(), this.renderers),
                new CategoryViewCommand(this.registry, this.gui),
                new ReloadCommand(this)
        );
        beans.forEach(bean -> manager.command(bean.configure(rootCommand)));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
