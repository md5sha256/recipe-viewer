package io.github.md5sha256.recipeviewer.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record NexoItemStack(String itemId) implements ItemStackConfig {

    @Override
    public @NotNull ItemStackConfigType configType() {
        return ItemStackConfigType.NEXO_ITEM;
    }
}
