package org.c4i.nlp.ph3.tokenize;

import org.c4i.nlp.ph3.normalize.StringNormalizers;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Character.*;

/**
 * Useful common word predicates.
 * Created by arvid on 12-5-15.
 */
public class WordPredicates {

    private static final Pattern PUNCT_PATTERN = Pattern.compile("^\\W$");
    private static final Pattern CONTAINS_DIGITS_PATTERN = Pattern.compile("\\d");
    private static final Pattern WORD_OR_NAME_LIKE = Pattern.compile(
            "((\\p{L}\\p{M}*)([\\p{L}\\p{M}'\\- ]*(\\p{L}\\p{M}*))*)" // a word, with space, ' or - in the middle
            + "( ([A-Z])|[A-Z]([0-9])+|([0-9])+\\w+)?" // possibly ending in a "model" name (B, XC90)
    );

    public static final Predicate<String> SENSIBLE =
            longerThan(2)
            .and(shorterThan(50))
            .and(wordOrNameLike())
            .and(randomString().negate());

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

    public static Predicate<String> number(){
        return word -> RegexUtil.NUMBER.matcher(word).find();
    }

    public static Predicate<String> wordOrNameLike(){
        return word -> WORD_OR_NAME_LIKE.matcher(word).find();
    }

    public static Predicate<String> containsDigits(){
        return word -> CONTAINS_DIGITS_PATTERN.matcher(word).find();
    }

    public static Predicate<String> punctuation(){
        return word -> PUNCT_PATTERN.matcher(word).find();
    }

    /**
     * Heuristic way of identifying non-words (randomString strings), by finding
     * mix of digits and alphas in abnormal way, or a strange mix of numbers and other chars.
     * Should still return false for likely product serie numbers: xc100, 5000n
     * @return test for "abnormality" of a given word.
     */
    public static Predicate<String> randomString(){
        return text -> {
            boolean result = false;
            wordLoop: for (String word : text.split(" ")) {

                if (word.length() < 6) {
                    result |= false;
                    continue;
                }
                word = StringNormalizers.NO_ACCENTS.normalize(word);
                char[] chars = word.toCharArray();

                boolean startUppercase = isUpperCase(chars[0]);
                boolean startNumber = isDigit(chars[0]);
                boolean sawAlpha = isAlphabetic(chars[0]);
                boolean sawLowerCase = sawAlpha && !startUppercase;
                boolean sawVowel = isVowel(chars[0]);
                for (int i = 1; i < chars.length; i++) {
                    char c = chars[i];
                    boolean isUpperCase = isUpperCase(c);
                    boolean isDigit = isDigit(c);
                    boolean isAlpha = isAlphabetic(c);
                    sawLowerCase |= isAlpha && !isUpperCase;
                    sawAlpha |= isAlpha;
                    if (sawLowerCase && isUpperCase) {
                        // mix of upper and lowercase in abnormal way (all upper = ok, start upper = ok)
                        result |= true;
                        break wordLoop;
                    }

                    if (sawAlpha && isDigit) {
                        for (int j = i + 1; j < chars.length; j++) {
                            if (!isDigit(chars[j])) {
                                // mix of digits and alphas in abnormal way
                                result |= true;
                                break wordLoop;
                            }
                        }
                        result |= false;
                        continue;
                    }

                    sawVowel |= isVowel(c);
                }

                result |= !sawVowel && !startNumber;

            }
            return result;
        };
    }

    public static boolean isVowel(char c) {
        return "AEIOUaeiou".indexOf(c) != -1;
    }

    public static Predicate<String> shorterThan(int maxLengthExclusive){
        return word -> word.length() < maxLengthExclusive;
    }

    public static Predicate<String> longerThan(int minLengthExclusive){
        return word -> word.length() > minLengthExclusive;
    }

    public static List<String> filter(List<String> words, Predicate<String> predicate){
        return words.stream().filter(predicate).collect(Collectors.toList());
    }

    public static List<String> filterTokens(List<Token> words, Predicate<String> predicate){
        return words.stream().map(Token::getWord).filter(predicate).collect(Collectors.toList());
    }

}
