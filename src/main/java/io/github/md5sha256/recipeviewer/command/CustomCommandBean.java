package io.github.md5sha256.recipeviewer.command;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;

public abstract class CustomCommandBean<C> {

    public abstract Command.Builder<? extends C> configure(Command.@NonNull Builder<C> builder);

}
