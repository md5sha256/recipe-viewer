package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.gui.RecipeGUI;
import io.github.md5sha256.recipeviewer.model.RecipeList;
import io.github.md5sha256.recipeviewer.renderer.Renderers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Keyed;
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
import org.incendo.cloud.parser.standard.StringParser;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeSearchCommand extends CustomCommandBean<Source> {

    private static final CloudKey<String> KEY_RECIPE = CloudKey.of("recipe", String.class);

    private final Server server;
    private final RecipeGUI gui;

    public RecipeSearchCommand(@Nonnull Server server,
                               @Nonnull RecipeGUI gui) {
        this.server = server;
        this.gui = gui;
    }

    @Override
    public Command.Builder<? extends Source> configure(Command.@NonNull Builder<Source> builder) {
        return builder
                .literal("recipesearch")
                .permission("recipeviewer.recipesearch")
                .senderType(PlayerSource.class)
                .optional(KEY_RECIPE, StringParser.stringParser())
                .handler(this::handleCommand);
    }

    private List<Recipe> findRecipes(@Nonnull String target) {
        List<Recipe> recipes = new ArrayList<>();
        Iterator<Recipe> iterator = this.server.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe instanceof Keyed keyed && keyed.getKey().value().contains(target)) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }


    private void handleCommand(@NonNull CommandContext<PlayerSource> context) {
        Player player = context.sender().source();
        Component title;
        List<Recipe> recipes;
        if (context.contains(KEY_RECIPE)) {
            String searchTerm = context.get(KEY_RECIPE);
            title = Component.text("Results for: " + searchTerm);
            recipes = findRecipes(searchTerm);
        } else {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                player.sendMessage(Component.text(
                        "Hold an item in your hand or enter a search query!",
                        NamedTextColor.RED));
                return;
            }
            Component itemName = this.server.getItemFactory().displayName(item);
            title = Component.text("Recipes for: ").append(itemName);
            recipes = this.server.getRecipesFor(item);
        }
        if (recipes.isEmpty()) {
            player.sendMessage(Component.text("No recipes found!", NamedTextColor.RED));
        }
        this.gui.createGui(title, List.of(new RecipeList(recipes)), null).show(player);
    }
}
