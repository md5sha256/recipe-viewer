package io.github.md5sha256.recipeviewer;

import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NexoFeature {

    private final NexoItemProvider reflectionInfo;

    public NexoFeature(@Nonnull Server server) {
        PluginManager pluginManager = server.getPluginManager();
        if (!pluginManager.isPluginEnabled("Nexo")) {
            this.reflectionInfo = null;
        } else {
            Plugin nexoPlugin = pluginManager.getPlugin("Nexo");
            if (nexoPlugin == null) {
                this.reflectionInfo = null;
            } else {
                this.reflectionInfo = new NexoCompat();
            }
        }
    }

    public boolean isDisabled() {
        return this.reflectionInfo == null;
    }

    @Nullable
    public ItemStack getItemFromId(@Nonnull String id) {
        if (this.reflectionInfo == null) {
            return null;
        }
        return this.reflectionInfo.getItemStack(id);
    }

    @FunctionalInterface
    private interface NexoItemProvider {
        @Nullable ItemStack getItemStack(@Nonnull String nexoId);
    }


    private static class NexoCompat implements NexoItemProvider {

        @Nullable
        public ItemStack getItemStack(@Nonnull String nexoId) {
            com.nexomc.nexo.items.ItemBuilder builder = com.nexomc.nexo.api.NexoItems.itemFromId(nexoId);
            if (builder == null) {
                return null;
            }
            return builder.getFinalItemStack();
        }

    }

}
