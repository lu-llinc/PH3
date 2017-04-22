package org.c4i.nlp.ph3.normalize;

import java.util.regex.Pattern;

import static java.text.Normalizer.Form;
import static java.text.Normalizer.normalize;

/**
 * Collection of common string normalizations.
 * @author Arvid Halma
 * @version 9-4-2015 - 20:46
 */
public class StringNormalizers {

    private static final Pattern IN_COMBINING_DIACRITICAL_MARKS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern BETWEEN_BRACKETS_PATTERN = Pattern.compile("(\\(.*?\\))|(\\[.*?\\])|(\\{.*?\\})");
    private static final Pattern NON_ALPHA_NUM_PATTERN = Pattern.compile("[^\\p{L}\\p{M}[0-9]]");


    public static StringNormalizer LOWER_CASE = String::toLowerCase;

    public static StringNormalizer TRIMMED = String::trim;

    public static StringNormalizer NO_BRACKETS = s -> BETWEEN_BRACKETS_PATTERN.matcher(s).replaceAll("");

    public static StringNormalizer UNICODE = s -> normalize(s, Form.NFD);

    public static StringNormalizer NO_ACCENTS = s -> IN_COMBINING_DIACRITICAL_MARKS_PATTERN.matcher(UNICODE.normalize(s)).replaceAll("");

//    public static StringNormalizer DEFAULT = s -> NO_ACCENTS.normalize(NON_ALPHA_NUM_PATTERN.matcher(s.trim()).replaceAll("").toLowerCase());

    public static StringNormalizer ALPHA_NUM_ONLY = s -> NON_ALPHA_NUM_PATTERN.matcher(s).replaceAll("");

    public static StringNormalizer DEFAULT = TRIMMED.andThen(ALPHA_NUM_ONLY).andThen(LOWER_CASE).andThen(UNICODE).andThen(NO_ACCENTS);

    public static StringNormalizer STEMMED = new PorterStemmer();

    public static StringNormalizer STEMMED_I18N = new ArabicStemmer().compose(new PorterStemmer());

    public static StringNormalizer DEFAULT_STEMMED = STEMMED.compose(DEFAULT);

    public static StringNormalizer DEFAULT_STEMMED_I18N = STEMMED_I18N.compose(new ArabicNormalizer()).compose(DEFAULT);


}
