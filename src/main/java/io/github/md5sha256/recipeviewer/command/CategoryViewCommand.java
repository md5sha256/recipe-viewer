package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.CategoryRegistry;
import io.github.md5sha256.recipeviewer.gui.RecipeGUI;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bean.CommandBean;
import org.incendo.cloud.bean.CommandProperties;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import javax.annotation.Nonnull;

public class CategoryViewCommand extends CommandBean<Source> {

    private static final CloudKey<RecipeCategory> KEY_CATEGORY = CloudKey.of("category",
            RecipeCategory.class);
    private final CategoryRegistry registry;
    private final RecipeGUI gui;

    public CategoryViewCommand(@Nonnull CategoryRegistry registry, @Nonnull RecipeGUI gui) {
        this.registry = registry;
        this.gui = gui;
    }

    @Override
    protected @Nonnull CommandProperties properties() {
        return CommandProperties.of("recipecategoryview", "rcv");
    }

    @Override
    protected @Nonnull Command.Builder<? extends Source> configure(@Nonnull Command.Builder<Source> builder) {
        return builder.senderType(PlayerSource.class)
                .required(KEY_CATEGORY, CategoryParser.categoryParser(this.registry))
                .handler(this::handleCommand);
    }

    private void handleCommand(@Nonnull CommandContext<PlayerSource> context) {
        RecipeCategory category = context.get(KEY_CATEGORY);
        Player player = context.sender().source();
        this.gui.createGui(category).show(player);
    }
}
