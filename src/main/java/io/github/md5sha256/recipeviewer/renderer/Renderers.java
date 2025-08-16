package io.github.md5sha256.recipeviewer.renderer;

import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Renderers {

    private final List<RenderMeta> renderers = new ArrayList<>();

    public <T extends Recipe> Renderers registerRenderer(
            @Nonnull Class<T> recipeClass,
            @Nonnull RecipeRenderer<T> renderer
    ) {
        this.renderers.add(new RenderMeta(recipeClass, renderer));
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean tryRenderRecipe(@Nonnull Server server,
                                   @Nonnull HumanEntity player,
                                   @Nonnull Recipe recipe) {
        for (RenderMeta meta : renderers) {
            if (meta.recipeClass().isInstance(recipe)) {
                RecipeRenderer erasedRenderer = meta.renderer();
                Inventory inventory = erasedRenderer.renderRecipe(server, recipe).getInventory();
                player.openInventory(inventory);
                return true;
            }
        }
        return false;
    }


    private record RenderMeta(@Nonnull Class<?> recipeClass, @Nonnull RecipeRenderer<?> renderer) {

    }

}
