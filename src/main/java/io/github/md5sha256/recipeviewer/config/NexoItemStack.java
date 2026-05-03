package io.github.md5sha256.recipeviewer.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;

@ConfigSerializable
public record NexoItemStack(@Setting("item-id") @Required @Nonnull String itemId) implements ItemStackConfig {

    @Override
    public @NotNull ItemStackConfigType configType() {
        return ItemStackConfigType.NEXO_ITEM;
    }
}
