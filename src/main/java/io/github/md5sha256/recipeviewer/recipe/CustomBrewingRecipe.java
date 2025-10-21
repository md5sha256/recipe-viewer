package io.github.md5sha256.recipeviewer.recipe;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record CustomBrewingRecipe(
        @Nonnull List<ItemStack> inputs,
        @Nullable ItemStack ingredient,
        @Nonnull List<ItemStack> outputs,
        boolean consumeIngredient
) {

    public CustomBrewingRecipe {
        if (inputs.size() > 3) {
            throw new IllegalArgumentException("Cannot have more than 3 inputs!");
        }
        if (outputs.size() > 3) {
            throw new IllegalArgumentException("Cannot have more than 3 outputs!");
        }
    }

    @Override
    @Nonnull
    public List<ItemStack> inputs() {
        List<ItemStack> copy = new ArrayList<>(this.inputs.size());
        for (ItemStack itemStack : this.inputs) {
            copy.add(itemStack.clone());
        }
        return copy;
    }

    @Override
    @Nullable
    public ItemStack ingredient() {
        return ingredient == null ? null : ingredient.clone();
    }

    @Override
    @Nonnull
    public List<ItemStack> outputs() {
        List<ItemStack> copy = new ArrayList<>(this.outputs.size());
        for (ItemStack itemStack : this.outputs) {
            copy.add(itemStack.clone());
        }
        return copy;
    }
}
