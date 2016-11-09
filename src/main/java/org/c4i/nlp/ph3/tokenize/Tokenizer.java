package org.c4i.nlp.ph3.tokenize;

import java.util.ArrayList;
import java.util.List;

/**
 * From a text to a list of words.
 * @author Arvid Halma
 * @version 12-5-15.
 */
public interface Tokenizer {
    default List<Token> tokenize(String text){
        return tokenize(text.split("\\s+"));
    }

    static List<Token> tokenize(String[] words){
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            tokens.add(new Token(word, i));
        }
        return tokens;
    }

    static List<Token> tokenize(List<String> words){
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            tokens.add(new Token(word, i));
        }
        return tokens;
    }
}
