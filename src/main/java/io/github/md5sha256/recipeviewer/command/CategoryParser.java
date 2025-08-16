package io.github.md5sha256.recipeviewer.command;

import io.github.md5sha256.recipeviewer.CategoryRegistry;
import io.github.md5sha256.recipeviewer.model.RecipeCategory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class CategoryParser<C> implements ArgumentParser<C, RecipeCategory>, BlockingSuggestionProvider<C> {

    private final CategoryRegistry registry;

    public CategoryParser(@NonNull CategoryRegistry registry) {
        this.registry = registry;
    }

    public static <C> ParserDescriptor<C, RecipeCategory> categoryParser(@NonNull CategoryRegistry registry) {
        return ParserDescriptor.of(new CategoryParser<>(registry), RecipeCategory.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull RecipeCategory> parse(@NonNull CommandContext<@NonNull C> commandContext,
                                                                       @NonNull CommandInput commandInput) {
        String name = commandInput.readString();
        return this.registry.getByName(name)
                .map(ArgumentParseResult::success)
                .orElseGet(() -> ArgumentParseResult.failure(new IllegalArgumentException(
                        "Unknown category: " + name)));
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this;
    }

    @Override
    public @NonNull Iterable<? extends @NonNull Suggestion> suggestions(@NonNull CommandContext<C> context,
                                                                        @NonNull CommandInput input) {
        String rawInput = input.peekString();
        return registry.categoryNames().stream()
                .filter(name -> name.startsWith(rawInput))
                .map(Suggestion::suggestion)
                .toList();
    }
}
