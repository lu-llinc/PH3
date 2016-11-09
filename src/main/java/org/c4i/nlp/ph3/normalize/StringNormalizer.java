package org.c4i.nlp.ph3.normalize;

import org.c4i.nlp.ph3.tokenize.Token;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * String normalization interface.
 * @author Arvid Halma
 * @version 9-4-2015 - 20:20
 */
public interface StringNormalizer extends Function<String, String>{
    StringNormalizer DEFAULT = word -> word;

    String normalize(String string);

    default Token normalize(Token token){
        token.setNormalizedWord(normalize(token.getWord()));
        return token;
    }

    default String apply(String word){
        return normalize(word);
    }


    default StringNormalizer compose(StringNormalizer before) {
        final StringNormalizer org = this;
        return string -> org.normalize(before.normalize(string));
    }

    default StringNormalizer andThen(StringNormalizer after) {
        final StringNormalizer org = this;
        return string -> after.normalize(org.normalize(string));
    }

    default List<String> normalize(Collection<String> words){
        return words.stream().map(this::normalize).collect(Collectors.toList());
    }

    default List<Token> normalizeTokens(Collection<Token> words){
        return words.stream().map(this::normalize).collect(Collectors.toList());
    }

    default void normalizeTokens(Token[] words){
        for (Token word : words) {
            normalize(word);
        }
    }

    default List<String> normalizeTokensToWords(Collection<Token> words){
        return words.stream().map(token -> normalize(token).getWord()).collect(Collectors.toList());
    }
}
