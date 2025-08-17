package io.github.md5sha256.recipeviewer.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.PagingButtons;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.github.md5sha256.recipeviewer.CategoryRegistry;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeCategoryPointer;
import io.github.md5sha256.recipeviewer.model.RecipeElement;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import io.github.md5sha256.recipeviewer.renderer.Renderers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeGUI {

    private final Renderers renderers;
    private final CategoryRegistry registry;
    private final Plugin plugin;
    private final Server server;

    public RecipeGUI(@Nonnull Renderers renderers,
                     @Nonnull CategoryRegistry registry,
                     @Nonnull Plugin plugin,
                     @Nonnull Server server) {
        this.renderers = renderers;
        this.registry = registry;
        this.plugin = plugin;
        this.server = server;
    }

    public ChestGui createGui(@Nonnull RecipeCategory category) {
        ChestGui gui = new ChestGui(6, ComponentHolder.of(category.displayName()), this.plugin);
        List<GuiItem> items = new ArrayList<>();
        for (RecipeElement element : category.elements()) {
            if (element instanceof RecipeCategoryPointer(String categoryName)) {
                Optional<RecipeCategory> optional = this.registry.getByName(categoryName);
                if (optional.isEmpty()) {
                    this.plugin.getLogger().warning("Missing category: " + categoryName);
                    continue;
                }
                RecipeCategory recipeCategory = optional.get();
                GuiItem item = createCategoryItem(recipeCategory);
                item.setAction(event -> {
                    ChestGui subcatGui = createGui(recipeCategory);
                    subcatGui.show(event.getWhoClicked());
                });
                items.add(item);
            } else if (element instanceof RecipeList(List<Recipe> recipes)) {
                items.addAll(recipes.stream().map(this::createRecipeItem).toList());
            }
        }
        PaginatedPane mainPane = new PaginatedPane(9, 5);
        mainPane.populateWithGuiItems(items);


        StaticPane footerPane = getFooterPane();

        ItemStack fillItem = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE);
        fillItem.editMeta(meta -> meta.displayName(Component.empty()));
        footerPane.fillWith(fillItem, null, this.plugin);

        PagingButtons pagingButtons = getPagingButtons(5, mainPane);



        gui.addPane(mainPane);
        gui.addPane(footerPane);
        gui.addPane(pagingButtons);
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        return gui;
    }

    private @NotNull StaticPane getFooterPane() {
        StaticPane footerPane = new StaticPane(0, 5, 9, 1, Pane.Priority.LOWEST);
        ItemStack backItem = ItemStack.of(Material.ARROW);
        backItem.editMeta(meta -> {
            Component displayName = Component.text("Back", NamedTextColor.RED)
                    .decoration(TextDecoration.ITALIC, false);
            meta.displayName(displayName);
        });
        GuiItem backButton = new GuiItem(backItem, event -> {
            event.getView().close();
        }, this.plugin);
        footerPane.addItem(backButton, 4, 0);
        return footerPane;
    }

    private @NotNull PagingButtons getPagingButtons(int y, PaginatedPane mainPane) {
        PagingButtons pagingButtons = new PagingButtons(Slot.fromXY(3, y),
                3,
                Pane.Priority.HIGH,
                mainPane,
                this.plugin);
        Component nextPageComp = Component.text("Next Page")
                .decoration(TextDecoration.ITALIC, false);
        Component prevPageComp = Component.text("Prev Page")
                .decoration(TextDecoration.ITALIC, false);
        ItemStack nextButton = ItemStack.of(Material.PAPER);
        nextButton.editMeta(meta -> meta.displayName(nextPageComp));
        ItemStack prevButton = ItemStack.of(Material.PAPER);
        prevButton.editMeta(meta -> meta.displayName(prevPageComp));
        pagingButtons.setForwardButton(new GuiItem(nextButton, this.plugin));
        pagingButtons.setBackwardButton(new GuiItem(prevButton, this.plugin));
        return pagingButtons;
    }

    private GuiItem createCategoryItem(@Nonnull RecipeCategory category) {
        ItemStack icon = category.icon();
        Component displayName = category.displayName().decoration(TextDecoration.ITALIC, false);
        icon.editMeta(meta -> {
            meta.displayName(displayName);
            meta.lore(null);
        });
        return new GuiItem(icon, this.plugin);
    }


    @Nonnull
    private GuiItem createRecipeItem(@Nonnull Recipe recipe) {
        ItemStack icon = recipe.getResult().asOne();
        return new GuiItem(icon, event -> {
            event.getView().close();
            if (!this.renderers.tryRenderRecipe(this.server, event.getWhoClicked(), recipe)) {
                Component msg = Component.text("Recipe type not supported: " + recipe.getClass(),
                        NamedTextColor.RED);
                event.getWhoClicked().sendMessage(msg);
            }
        }, this.plugin);
    }

}
