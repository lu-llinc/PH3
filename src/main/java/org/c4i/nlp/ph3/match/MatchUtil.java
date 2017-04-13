package org.c4i.nlp.ph3.match;

import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.c4i.nlp.ph3.normalize.StringNormalizers;
import org.c4i.nlp.ph3.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.Token;

import java.util.List;

/**
 * One-stop shop for matching phrase patterns to texts.
 * @version 8-11-16
 * @author Arvid Halma
 */
public class MatchUtil {

    public static StringNormalizer DEFAULT_NORMALIZER = StringNormalizers.DEFAULT;

    /**
     * Tokenizes a text using {@link MatchingWordTokenizer}.
     * @param text original text
     * @param normalizer how words are preprocessed
     * @return an array of separate 'words'
     */
    public static Token[] textToTokens(String text, StringNormalizer normalizer){
        List<Token> textTokens = new MatchingWordTokenizer().tokenize(text);
        Token[] tokens = textTokens.toArray(new Token[textTokens.size()]);
        normalizer.normalizeTokens(tokens);
        return tokens;
    }

    /**
     * Tokenizes a text using {@link MatchingWordTokenizer}.
     * Words are normalized with {@link StringNormalizers#DEFAULT}.
     * @param text original text
     * @return an array of separate 'words'
     */
    public static Token[] textToTokens(String text){
        return textToTokens(text, DEFAULT_NORMALIZER);
    }

    /**
     * Compiles a phrase rule to a form that can efficiently be reused when matching.
     * @param pattern a phrase expression
     * @param normalizer how words are preprocessed
     * @return an expression in Conjunctive Normal Form
     */
    public static Literal[][] compilePattern(String pattern, StringNormalizer normalizer){
        return MatchParser.compileBody(pattern, true, normalizer);
    }

    /**
     * Compiles a phrase rule to a form that can efficiently be reused when matching.
     * @param pattern a phrase expression
     * @return an expression in Conjunctive Normal Form
     */
    public static Literal[][] compilePattern(String pattern){
        return MatchParser.compileBody(pattern, true, DEFAULT_NORMALIZER);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static int[] findRange(final Token[] text, final Literal[][] pattern){
        return MatchEval.findRange(text, pattern, null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static int[] findRange(final String text, final Literal[][] pattern){
        return MatchEval.findRange(textToTokens(text), pattern, null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static int[] findRange(final String text, final String pattern){
        return MatchEval.findRange(textToTokens(text), compilePattern(pattern), null);
    }

    /**
     * Tries to finds the first possible occurrence of the pattern in the text.
     * @param text original text
     * @param pattern a phrase expression
     * @param normalizer how words are preprocessed and compared
     * @return a array of length 2: {token_start_index_inclusive, token_end_index_exclusive}, or null when the pattern was not found
     */
    public static int[] findRange(final String text, final String pattern, final StringNormalizer normalizer){
        return MatchEval.findRange(textToTokens(text, normalizer), compilePattern(pattern, normalizer), null);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, String pattern){
        Token[] tokens = textToTokens(text);
        return contains(tokens, pattern, DEFAULT_NORMALIZER);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, String pattern, StringNormalizer normalizer){
        Token[] tokens = textToTokens(text, normalizer);
        return contains(tokens, pattern, normalizer);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(Token[] text, String pattern, StringNormalizer normalizer){
        Literal[][] cnf = compilePattern(pattern, normalizer);
        return MatchEval.contains(text, cnf);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @param normalizer how words are preprocessed and compared
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(String text, Literal[][] pattern, StringNormalizer normalizer){
        Token[] tokens = textToTokens(text, normalizer);
        return contains(tokens, pattern);
    }

    /**
     * Checks whether a given pattern occurs in the text.
     * @param text the content to be queried
     * @param pattern the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean contains(Token[] text, Literal[][] pattern){
        return MatchEval.contains(text, pattern);
    }

    /**
     * Checks whether at least one of the given patterns occurs in the text.
     * @param text the content to be queried
     * @param patterns the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean containsAny(Token[] text, List<Literal[][]> patterns){
        return patterns.stream().anyMatch(p -> contains(text, p));
    }

    /**
     * Checks whether all of the given patterns occurs in the text.
     * @param text the content to be queried
     * @param patterns the expression to look for
     * @return true if the pattern is found, false otherwise
     */
    public static boolean containsAll(Token[] text, List<Literal[][]> patterns){
        return patterns.stream().allMatch(p -> contains(text, p));
    }
}
