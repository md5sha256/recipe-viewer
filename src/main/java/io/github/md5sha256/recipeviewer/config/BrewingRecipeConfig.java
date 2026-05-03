package io.github.md5sha256.recipeviewer.config;

import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public record BrewingRecipeConfig(
        @Nonnull @Setting("inputs") List<ItemStackConfig> inputs,
        @Nullable @Setting("ingredient") ItemStackConfig ingredient,
        @Nonnull @Setting("outputs") List<ItemStackConfig> outputs,
        @Nullable @Setting("consume-ingredient") Boolean consumeIngredient
) {

    public BrewingRecipeConfig {
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
    }

    public boolean consumeIngredientValue() {
        return !Boolean.FALSE.equals(this.consumeIngredient);
    }
}
