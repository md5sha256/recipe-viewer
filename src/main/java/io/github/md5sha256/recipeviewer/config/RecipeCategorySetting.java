package io.github.md5sha256.recipeviewer.config;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import java.util.List;

@ConfigSerializable
public record RecipeCategorySetting(
        @Nonnull @Setting("name") String name,
        @Nonnull @Setting("displayName") Component displayName,
        @Nonnull @Setting("elements") List<RecipeSetting> elements
) {
}
