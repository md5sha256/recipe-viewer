package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.renderer.Renderers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import javax.annotation.Nonnull;

public class RecipeViewCommand extends CustomCommandBean<Source> {

    private static final CloudKey<Recipe> KEY_RECIPE = CloudKey.of("recipe", Recipe.class);

    private final Server server;
    private final Renderers renderers;

    public RecipeViewCommand(@Nonnull Server server, @Nonnull Renderers renderers) {
        this.server = server;
        this.renderers = renderers;
    }

    @Override
    public Command.Builder<? extends Source> configure(Command.@NonNull Builder<Source> builder) {
        return builder
                .literal("recipe")
                .permission("recipeviewer.recipe")
                .senderType(PlayerSource.class)
                .required(KEY_RECIPE, RecipeParser.recipeParser(this.server))
                .handler(this::handleCommand);
    }

    private void handleCommand(@NonNull CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        Recipe recipe = context.get(KEY_RECIPE);
        if (this.renderers.tryRenderRecipe(this.server, player, recipe)) {
            return;
        }
        player.sendMessage(Component.text("Recipe type not supported: " + recipe.getClass(),
                NamedTextColor.RED));
    }
}
