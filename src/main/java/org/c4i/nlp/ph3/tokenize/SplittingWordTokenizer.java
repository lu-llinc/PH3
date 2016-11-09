package org.c4i.nlp.ph3.tokenize;

import org.c4i.nlp.ph3.tokenize.Token;
import org.c4i.nlp.ph3.tokenize.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Retrieve list of words from a text. The text is split on whitespace, punctuation and the like.
 * @author Arvid Halma
 * @version 10-5-2015 - 20:55
 */
public class SplittingWordTokenizer implements Tokenizer {
    public SplittingWordTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text){
        StringTokenizer tokenizer = new StringTokenizer(text, " \t\n\r\f,.:;?![](){}&|/\\<=>");
        List<Token> words = new ArrayList<>();
        int loc = 0;
        while (tokenizer.hasMoreElements()) {
            String word = tokenizer.nextToken();
            words.add(new Token(word, null, loc++, null, null, 1.0));
        }
        return words;
    }
}
