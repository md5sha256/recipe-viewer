package io.github.md5sha256.recipeviewer.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@ConfigSerializable
public record BrewingRecipeConfig(
        @Setting("inputs") @Required @Nonnull List<ItemStackConfig> inputs,
        @Nullable @Setting("ingredient") ItemStackConfig ingredient,
        @Setting("outputs") @Required @Nonnull List<ItemStackConfig> outputs,
        @Setting("consume-ingredient") @Required boolean consumeIngredient
) {

    public BrewingRecipeConfig {
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
    }
}
