package io.github.md5sha256.recipeviewer;

import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class NexoFeature {

    private final NexoReflectionInfo reflectionInfo;

    public NexoFeature(@Nonnull Server server) {
        PluginManager pluginManager = server.getPluginManager();
        if (!pluginManager.isPluginEnabled("Nexo")) {
            this.reflectionInfo = null;
        } else {
            Plugin nexoPlugin = pluginManager.getPlugin("Nexo");
            if (nexoPlugin == null) {
                this.reflectionInfo = null;
            } else {
                NexoReflectionInfo info;
                try {
                    info = new NexoReflectionInfo(nexoPlugin);
                } catch (ReflectiveOperationException ex) {
                    ex.printStackTrace();
                    info = null;
                }
                this.reflectionInfo = info;
            }
        }
    }

    public boolean isDisabled() {
        return this.reflectionInfo == null;
    }

    @Nullable
    public ItemStack getItemFromId(@Nonnull Logger logger, @Nonnull String id) {
        if (this.reflectionInfo == null) {
            return null;
        }
        return this.reflectionInfo.getItemStack(logger, id);
    }


    private static class NexoReflectionInfo {

        private final MethodHandle itemFromIdMethod;
        private final MethodHandle getFinalItemStackMethod;

        public NexoReflectionInfo(@Nonnull Plugin nexoPlugin) throws ReflectiveOperationException {
            ClassLoader classLoader = nexoPlugin.getClass().getClassLoader();
            Class<?> nexoClass = Class.forName("com.nexomc.nexo.api.NexoItems", false, classLoader);
            Class<?> itemBuilderClass = Class.forName("com.nexomc.nexo.items.ItemBuilder",
                    false,
                    classLoader);
            Method itemfromIdMethod = nexoClass.getMethod("itemFromId", String.class);
            Method getFinalItemStackMethod = itemBuilderClass.getMethod("getFinalItemStack");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            this.itemFromIdMethod = lookup.unreflect(itemfromIdMethod);
            this.getFinalItemStackMethod = lookup.unreflect(getFinalItemStackMethod);
        }

        @Nullable
        public ItemStack getItemStack(@Nonnull Logger logger, @Nonnull String nexoId) {
            try {
                Object builder = this.itemFromIdMethod.invoke(nexoId);
                if (builder == null) {
                    return null;
                }
                return (ItemStack) this.getFinalItemStackMethod.invoke(builder);
            } catch (Throwable ex) {
                logger.warning("Failed to get nexo item!");
                ex.printStackTrace();
                return null;
            }
        }

    }

}
