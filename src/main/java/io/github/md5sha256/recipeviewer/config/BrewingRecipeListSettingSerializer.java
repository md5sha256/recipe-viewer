package io.github.md5sha256.recipeviewer.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeListSettingSerializer implements TypeSerializer<BrewingRecipeListSetting> {

    @Override
    public BrewingRecipeListSetting deserialize(@NonNull Type type,
                                                  @NonNull ConfigurationNode node) throws SerializationException {
        List<BrewingRecipeConfig> recipes = new ArrayList<>();
        ConfigurationNode listNode = node.node("brewing-recipes");
        if (!listNode.virtual()) {
            for (ConfigurationNode child : listNode.childrenList()) {
                recipes.add(child.require(BrewingRecipeConfig.class));
            }
        }
        return new BrewingRecipeListSetting(recipes);
    }

    @Override
    public void serialize(
            @NonNull Type type,
            @Nullable BrewingRecipeListSetting obj,
            @NonNull ConfigurationNode node
    ) throws SerializationException {
        if (obj == null) {
            return;
        }
        ConfigurationNode listNode = node.node("brewing-recipes");
        int i = 0;
        for (BrewingRecipeConfig recipe : obj.brewingRecipes()) {
            listNode.node(i++).set(BrewingRecipeConfig.class, recipe);
        }
    }
}
