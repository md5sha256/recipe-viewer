package io.github.md5sha256.recipeviewer.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;


public class ItemStackConfigSerializer implements TypeSerializer<ItemStackConfig> {

    private static final String TYPE_KEY = "type";

    @Override
    public ItemStackConfig deserialize(@NonNull Type type,
                                       @NonNull ConfigurationNode node) throws SerializationException {
        ItemStackConfigType configType = node.node(TYPE_KEY).get(ItemStackConfigType.class);
        if (configType == ItemStackConfigType.SIMPLE_ITEM) {
            return node.get(SimpleItemStack.class);
        } else if (configType == ItemStackConfigType.NEXO_ITEM) {
            return node.get(NexoItemStack.class);
        } else {
            throw new SerializationException("Unsupported config type: " + configType);
        }
    }

    @Override
    public void serialize(
            @NonNull Type type,
            @Nullable ItemStackConfig obj,
            @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            return;
        }
        node.node(TYPE_KEY).set(obj.configType());
        if (obj instanceof NexoItemStack nexoItemStack) {
            node.set(NexoItemStack.class, nexoItemStack);
        } else if (obj instanceof SimpleItemStack simpleItemStack) {
            node.set(SimpleItemStack.class, simpleItemStack);
        }
    }
}
