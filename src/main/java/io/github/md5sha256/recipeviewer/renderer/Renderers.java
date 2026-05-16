package io.github.md5sha256.recipeviewer.renderer;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import io.github.md5sha256.recipeviewer.util.RecipeView;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Renderers {

    private final List<RenderMeta> renderers = new ArrayList<>();

    public <T> Renderers registerRenderer(
            @Nonnull Class<T> recipeClass,
            @Nonnull RecipeRenderer<T> renderer
    ) {
        this.renderers.add(new RenderMeta(recipeClass, renderer));
        return this;
    }

    public boolean tryRenderRecipe(@Nonnull Server server,
                                   @Nonnull HumanEntity player,
                                   @Nonnull Object recipe) {
        return tryRenderRecipe(server, player, recipe, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean tryRenderRecipe(@Nonnull Server server,
                                   @Nonnull HumanEntity player,
                                   @Nonnull Object recipe,
                                   @Nullable Gui returnGui) {
        for (RenderMeta meta : renderers) {
            if (meta.recipeClass().isInstance(recipe)) {
                RecipeRenderer erasedRenderer = meta.renderer();
                InventoryHolder holder = erasedRenderer.renderRecipe(server, recipe);
                if (returnGui != null && holder instanceof RecipeView recipeView) {
                    recipeView.setReturnGui(returnGui);
                }
                player.openInventory(holder.getInventory());
                return true;
            }
        }
        return false;
    }


    private record RenderMeta(@Nonnull Class<?> recipeClass, @Nonnull RecipeRenderer<?> renderer) {

    }

}
