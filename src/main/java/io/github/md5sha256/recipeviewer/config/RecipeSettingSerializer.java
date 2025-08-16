package io.github.md5sha256.recipeviewer.config;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public class RecipeSettingSerializer implements TypeSerializer<RecipeSetting> {

    public static final String TYPE_KEY = "type";

    @Override
    public RecipeSetting deserialize(
            @Nonnull Type type,
            @Nonnull ConfigurationNode node
    ) throws SerializationException {
        RecipeSettingType settingType = node.node(TYPE_KEY).get(RecipeSettingType.class);
        if (settingType == RecipeSettingType.RECIPE_LIST) {
            return node.get(RecipeListSetting.class);
        } else if (settingType == RecipeSettingType.CATEGORY_NAME) {
            return node.get(RecipeCategoryName.class);
        } else {
            throw new SerializationException("Unknown setting type: " + settingType);
        }
    }

    @Override
    public void serialize(
            @Nonnull Type type,
            @Nullable RecipeSetting obj,
            @Nonnull ConfigurationNode node
    ) throws SerializationException {
        if (obj == null) {
            return;
        }
        node.node(TYPE_KEY).set(RecipeSettingType.class, obj.settingType());
        if (obj instanceof RecipeCategoryName catName) {
            node.set(RecipeCategoryName.class, catName);
        } else if (obj instanceof RecipeListSetting recipeList) {
            node.set(RecipeListSetting.class, recipeList);
        }
    }
}
