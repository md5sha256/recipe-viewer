package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.RecipeViewerPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.bean.CommandBean;
import org.incendo.cloud.bean.CommandProperties;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.util.sender.Source;

import javax.annotation.Nonnull;

public class ReloadCommand extends CustomCommandBean<Source> {

    private final RecipeViewerPlugin plugin;

    public ReloadCommand(@Nonnull RecipeViewerPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public Command.@NonNull Builder<? extends Source> configure(Command.@NonNull Builder<Source> builder) {
        return builder.permission("recipeviewer.reload")
                .handler(this::handleCommand);
    }

    private void handleCommand(@Nonnull CommandContext<Source> context) {
        Audience audience = context.sender().source();
        this.plugin.reloadRegistry().whenComplete((unused, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                audience.sendMessage(Component.text("RecipeViewer failed to reload", NamedTextColor.RED));
            } else {
                audience.sendMessage(Component.text("RecipeViewer reloaded", NamedTextColor.GREEN));
            }
        });
    }
}
