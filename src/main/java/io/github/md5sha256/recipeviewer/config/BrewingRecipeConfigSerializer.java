package io.github.md5sha256.recipeviewer.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeConfigSerializer implements TypeSerializer<BrewingRecipeConfig> {

    @Override
    public BrewingRecipeConfig deserialize(@NonNull Type type,
                                           @NonNull ConfigurationNode node) throws SerializationException {
        List<ItemStackConfig> inputs = deserializeItemStackConfigs(node.node("inputs"));
        ConfigurationNode ingredientNode = node.node("ingredient");
        ItemStackConfig ingredient = ingredientNode.virtual()
                ? null
                : ingredientNode.require(ItemStackConfig.class);
        List<ItemStackConfig> outputs = deserializeItemStackConfigs(node.node("outputs"));
        Boolean consumeIngredient = node.node("consume-ingredient").get(Boolean.class);
        return new BrewingRecipeConfig(inputs, ingredient, outputs, consumeIngredient);
    }

    private static List<ItemStackConfig> deserializeItemStackConfigs(ConfigurationNode listNode)
            throws SerializationException {
        List<ItemStackConfig> list = new ArrayList<>();
        if (listNode.virtual()) {
            return list;
        }
        for (ConfigurationNode child : listNode.childrenList()) {
            list.add(child.require(ItemStackConfig.class));
        }
        return list;
    }

    @Override
    public void serialize(
            @NonNull Type type,
            @Nullable BrewingRecipeConfig obj,
            @NonNull ConfigurationNode node
    ) throws SerializationException {
        if (obj == null) {
            return;
        }
        serializeItemStackConfigs(node.node("inputs"), obj.inputs());
        if (obj.ingredient() != null) {
            node.node("ingredient").set(ItemStackConfig.class, obj.ingredient());
        }
        serializeItemStackConfigs(node.node("outputs"), obj.outputs());
        node.node("consume-ingredient").set(obj.consumeIngredient());
    }

    private static void serializeItemStackConfigs(ConfigurationNode listNode,
                                                  List<ItemStackConfig> configs
    ) throws SerializationException {
        for (int i = 0; i < configs.size(); i++) {
            listNode.node(i).set(ItemStackConfig.class, configs.get(i));
        }
    }
}
