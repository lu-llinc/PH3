package org.c4i.nlp.ph3.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve list of words from a text. The text is split on whitespace, punctuation and the like.
 * @author Arvid Halma
 * @version 10-5-2015 - 20:55
 */
public class SplittingWordTokenizer implements Tokenizer {
    private static final Pattern NON_SPLITTER_PATTERN = Pattern.compile(
            "[^ \\t\\n\\r\\f,.:;?!\\[\\](){}&|/<=>]+"

    );
    
    public SplittingWordTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text){
        Matcher matcher = NON_SPLITTER_PATTERN.matcher(text);
        List<Token> words = new ArrayList<>();
        int loc = 0;
        while (matcher.find()) {
            Token token = new Token(matcher.group().trim(), loc++);
            token.setCharStart(matcher.start()).setCharEnd(matcher.end());
            words.add(token);
        }
        return words;
    }
    
    
    public List<Token> tokenizeAlt(String text){
        StringTokenizer tokenizer = new StringTokenizer(text, " \t\n\r\f,.:;?![](){}&|/\\<=>");
        List<Token> words = new ArrayList<>();
        int loc = 0;
        while (tokenizer.hasMoreElements()) {
            String word = tokenizer.nextToken();
            Token token = new Token(word, null, loc++, null, null, 1.0);
            words.add(token);
        }
        return words;
    }
}
