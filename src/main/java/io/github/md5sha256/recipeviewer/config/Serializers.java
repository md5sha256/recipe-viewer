package io.github.md5sha256.recipeviewer.config;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class Serializers {

    private Serializers() {
    }

    public static TypeSerializerCollection createDefaults(@Nonnull Server server,
                                                          @Nonnull Logger logger) {
        return TypeSerializerCollection.defaults().childBuilder()
                // POJOs
                .register(Component.class, ComponentSerializer.MINI_MESSAGE)
                .register(Recipe.class, new RecipeSerializer(server, logger))
                .registerExact(RecipeSetting.class, new RecipeSettingSerializer())
                .registerExact(ItemStackConfig.class, new ItemStackConfigSerializer())
                .build();
    }

    public static YamlConfigurationLoader.Builder yamlLoader(@Nonnull Server server,
                                                             @Nonnull Logger logger) {
        return YamlConfigurationLoader.builder()
                .defaultOptions(options -> options.serializers(createDefaults(server, logger)))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2);
    }

}
