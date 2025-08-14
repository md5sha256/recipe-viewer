package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.renderer.ShapedRecipeRenderer;
import io.github.md5sha256.recipeviewer.renderer.ShapelessRecipeRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.bean.CommandBean;
import org.incendo.cloud.bean.CommandProperties;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

public class RecipeViewCommand extends CommandBean<Source> {

    private static final CloudKey<Recipe> KEY_RECIPE = CloudKey.of("recipe", Recipe.class);

    private final Server server;

    public RecipeViewCommand(Server server) {
        this.server = server;
    }

    @Override
    protected @NonNull CommandProperties properties() {
        return CommandProperties.of("recipeview", "rv");
    }

    @Override
    protected Command.@NonNull Builder<? extends Source> configure(Command.@NonNull Builder<Source> builder) {
        return builder.senderType(PlayerSource.class)
                .required(KEY_RECIPE, RecipeParser.recipeParser(this.server))
                .handler(this::handleCommand);
    }

    private void handleCommand(@NonNull CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        Recipe recipe = context.get(KEY_RECIPE);
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            Inventory inventory = new ShapedRecipeRenderer().renderRecipe(this.server, shapedRecipe)
                    .getInventory();
            player.openInventory(inventory);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            Inventory inventory = new ShapelessRecipeRenderer().renderRecipe(this.server,
                    shapelessRecipe).getInventory();
            player.openInventory(inventory);
        } else {
            player.sendMessage(Component.text("Recipe type not supported: " + recipe.getClass(),
                    NamedTextColor.RED));
        }
    }
}
