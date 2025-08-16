package io.github.md5sha256.recipeviewer.model;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public record NexoItemStack(String itemId, ItemStack itemStack) implements ItemStackConfig {

    public NexoItemStack(@Nonnull String itemId, @Nonnull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemId = itemId;
    }

    @Override
    public @Nonnull String itemId() {
        return this.itemId;
    }


    @Override
    public @NotNull ItemStack itemStack() {
        return this.itemStack;
    }
}
