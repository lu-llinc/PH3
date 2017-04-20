package org.c4i.nlp.ph3;


import org.c4i.nlp.ph3.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.SplittingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.Token;
import org.c4i.nlp.ph3.tokenize.WordPredicates;

import java.util.List;

/**
 * @author Arvid
 * @version 6-4-2015 - 12:06
 */
public class WordTokenizerTest {
    public static void main(String[] args) {
        String text;

        text = "Helloo there! I am @arvid, so you #know... Can't complain, 'can you' ?!? dgasd (gasd A)SDg asgd \nAsgd!! aap. A € is $1.30 my friend. 2x bla. 412 41.4 -56?";
        System.out.println("text = " + text);
        System.out.println("SplittingWordTokenizer.tokenize(text) = " + new SplittingWordTokenizer().tokenize(text));

        List<Token> tokens = new MatchingWordTokenizer().tokenize(text);
        System.out.println("MatchingWordTokenizer().tokenize(text) = " + tokens);
        System.out.println("WordPredicates.filterSensible(words) = " + WordPredicates.filterTokens(tokens, WordPredicates.SENSIBLE));
    }
}
