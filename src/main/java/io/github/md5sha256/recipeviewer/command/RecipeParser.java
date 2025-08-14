package io.github.md5sha256.recipeviewer.command;

import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.Recipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class RecipeParser<C> implements ArgumentParser<C, Recipe>, BlockingSuggestionProvider<C> {

    private final Server server;

    public RecipeParser(@NonNull Server server) {
        this.server = server;
    }

    public static <C> ParserDescriptor<C, Recipe> recipeParser(@NonNull Server server) {
        return ParserDescriptor.of(new RecipeParser<>(server), Recipe.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Recipe> parse(@NonNull CommandContext<@NonNull C> commandContext,
                                                               @NonNull CommandInput commandInput) {
        String raw = commandInput.read(commandInput.remainingLength());
        NamespacedKey key = NamespacedKey.fromString(raw);
        if (key == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid recipe key: " + raw));
        }
        Recipe recipe = this.server.getRecipe(key);
        if (recipe == null) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Unknown recipe: " + raw));
        }
        return ArgumentParseResult.success(recipe);
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this;
    }

    @Override
    public @NonNull Iterable<? extends @NonNull Suggestion> suggestions(@NonNull CommandContext<C> context,
                                                                        @NonNull CommandInput input) {
        String raw = input.peekString();
        var spliterator = Spliterators.spliteratorUnknownSize(this.server.recipeIterator(), Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false)
                .filter(Keyed.class::isInstance)
                .map(Keyed.class::cast)
                .map(Keyed::key)
                .map(Key::toString)
                .filter(key -> key.startsWith(raw))
                .map(Suggestion::suggestion)
                .toList();
    }
}
