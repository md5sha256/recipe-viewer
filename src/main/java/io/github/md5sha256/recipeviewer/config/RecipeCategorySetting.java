package io.github.md5sha256.recipeviewer.config;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import java.util.List;

@ConfigSerializable
public record RecipeCategorySetting(
        @Setting("name") @Required @Nonnull String name,
        @Setting("displayName") @Required @Nonnull Component displayName,
        @Setting("icon") @Required @Nonnull ItemStackConfig icon,
        @Setting("elements") @Required @Nonnull List<RecipeSetting> elements
) {
}
