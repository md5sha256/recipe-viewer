package io.github.md5sha256.recipeviewer.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.MasonryPane;
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
                    if (event.getClickedInventory() == null) {
                        return;
                    }
                    ChestGui subcatGui = createGui(recipeCategory);
                    subcatGui.setParent(gui);
                    subcatGui.show(event.getWhoClicked());
                });
                items.add(item);
            } else if (element instanceof RecipeList(List<Recipe> recipes)) {
                items.addAll(recipes.stream().map(this::createRecipeItem).toList());
            }
        }
        PaginatedPane mainPane = new PaginatedPane(9, 5);
        mainPane.populateWithGuiItems(items);


        MasonryPane footerMasonry = new MasonryPane(0, 5, 9, 1);
        StaticPane footerPane = new StaticPane(0, 0, 9, 1, Pane.Priority.LOWEST);
        ItemStack fillItem = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE);
        fillItem.editMeta(meta -> meta.displayName(Component.empty()));
        footerPane.fillWith(fillItem, null, this.plugin);

        PagingButtons pagingButtons = getPagingButtons(mainPane);

        footerMasonry.addPane(footerPane);
        footerMasonry.addPane(pagingButtons);

        gui.addPane(mainPane);
        gui.addPane(footerMasonry);

        gui.setOnGlobalClick(event -> event.setCancelled(true));
        return gui;
    }

    private @NotNull PagingButtons getPagingButtons(PaginatedPane mainPane) {
        PagingButtons pagingButtons = new PagingButtons(Slot.fromXY(3, 0),
                3,
                Pane.Priority.HIGH,
                mainPane,
                this.plugin);
        ItemStack nextButton = ItemStack.of(Material.PAPER);
        nextButton.editMeta(meta -> meta.displayName(Component.text("Next Page")));
        ItemStack prevButton = ItemStack.of(Material.PAPER);
        nextButton.editMeta(meta -> meta.displayName(Component.text("Previous Page")));
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
