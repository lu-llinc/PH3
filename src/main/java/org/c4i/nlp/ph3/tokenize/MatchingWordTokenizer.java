package org.c4i.nlp.ph3.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve list of words from a text. The text is split on whitespace, punctuation and the like.
 * @author Arvid Halma
 * @version 10-5-2015 - 20:55
 */
public class MatchingWordTokenizer implements Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
                      "(?<![\\\\p{L}\\\\p{M}0-9+\\-])" // left word boundary
                    + "([\\-+]?([0-9]+[0-9.,]*))" // number like
                    + "|((\\p{L}\\p{M}*)([\\p{L}\\p{M}']*(\\p{L}\\p{M}*))*)" // a word, with ' or - in the middle
                              + "|(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]+)" //hash tags
                              + "|(?:\\s|\\A|^)[@@]+([A-Za-z0-9-_]+)" //Twitter handles
                    + "(?![\\\\p{L}\\\\p{M}0-9])" // right word boundary

    );

    public MatchingWordTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text){
        Matcher matcher = TOKEN_PATTERN.matcher(text);
        List<Token> words = new ArrayList<>();
        int loc = 0;
        while (matcher.find()) {
            Token token = new Token(matcher.group().trim(), loc++);
            token.setCharStart(matcher.start()).setCharEnd(matcher.end());
            words.add(token);
        }
        return words;
    }
}
