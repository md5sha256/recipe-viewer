package io.github.md5sha256.recipeviewer.config;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class RecipeSerializer extends ScalarSerializer<Recipe> {

    private final Server server;
    private final Logger logger;

    public RecipeSerializer(@Nonnull Server server, @Nonnull Logger logger) {
        super(Recipe.class);
        this.server = server;
        this.logger = logger;
    }

    @Override
    public Recipe deserialize(@Nonnull Type type,
                              @Nonnull Object obj) throws SerializationException {
        if (!(obj instanceof String str)) {
            throw new SerializationException("Unsupported type: " + type);
        }
        NamespacedKey key = NamespacedKey.fromString(str);
        if (key == null) {
            throw new SerializationException("Invalid recipe key: " + str);
        }
        Recipe recipe = this.server.getRecipe(key);
        if (recipe == null) {
            this.logger.warning("Unknown recipe: " + str);
            return null;
        }
        return recipe;

    }

    @Override
    protected @Nonnull Object serialize(@Nonnull Recipe item,
                                        @Nonnull Predicate<Class<?>> typeSupported) {
        if (item instanceof Keyed keyed) {
            return keyed.getKey().asString();
        }
        throw new RuntimeException("Recipe is not keyed: " + item.getClass());
    }
}
