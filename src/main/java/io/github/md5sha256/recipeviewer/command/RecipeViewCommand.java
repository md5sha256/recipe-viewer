package io.github.md5sha256.recipeviewer.command;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import io.github.md5sha256.recipeviewer.gui.RecipeGUI;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import io.github.md5sha256.recipeviewer.renderer.Renderers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class RecipeViewCommand extends CustomCommandBean<Source> {

    private static final CloudKey<Recipe> KEY_RECIPE = CloudKey.of("recipe", Recipe.class);

    private final Server server;
    private final Renderers renderers;
    private final RecipeGUI gui;

    public RecipeViewCommand(@Nonnull Server server,
                             @Nonnull Renderers renderers,
                             @NotNull RecipeGUI gui) {
        this.server = server;
        this.renderers = renderers;
        this.gui = gui;
    }

    @Override
    public Command.Builder<? extends Source> configure(Command.@NonNull Builder<Source> builder) {
        return builder
                .literal("recipe")
                .permission("recipeviewer.recipe")
                .senderType(PlayerSource.class)
                .optional(KEY_RECIPE, RecipeParser.recipeParser(this.server))
                .handler(this::handleRecipeCommand);
    }

    private void handleWithRecipeKey(@Nonnull CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        Recipe recipe = context.get(KEY_RECIPE);
        if (this.renderers.tryRenderRecipe(this.server, player, recipe)) {
            return;
        }
        player.sendMessage(Component.text("Recipe type not supported: " + recipe.getClass(),
                NamedTextColor.RED));
    }

    private void handleWithoutRecipeKey(@Nonnull CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        ItemStack inMainHand = player.getInventory().getItemInMainHand();
        if (inMainHand.getType().isAir()) {
            player.sendMessage(Component.text("You must hold an item in your hand!",
                    NamedTextColor.RED));
            return;
        }
        List<Recipe> matchingRecipes = server.getRecipesFor(inMainHand);
        RecipeCategory results = new RecipeCategory("Results",
                Component.text("Results"),
                inMainHand,
                List.of(new RecipeList(matchingRecipes)));
        ChestGui resultUi = this.gui.createGui(results);
        resultUi.show(player);
    }

    private void handleRecipeCommand(@NonNull CommandContext<PlayerSource> context) {
        if (context.contains(KEY_RECIPE)) {
            handleWithRecipeKey(context);
        } else {
            handleWithoutRecipeKey(context);
        }
    }
}
