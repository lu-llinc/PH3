package org.c4i.nlp.ph3;

import org.apache.commons.lang3.time.StopWatch;
import org.c4i.nlp.ph3.match.Literal;
import org.c4i.nlp.ph3.match.MatchEval;
import org.c4i.nlp.ph3.match.MatchParser;
import org.c4i.nlp.ph3.match.MatchRule;
import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.c4i.nlp.ph3.normalize.StringNormalizers;
import org.c4i.nlp.ph3.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.Token;
import org.c4i.nlp.ph3.tokenize.Tokenizer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test match results.
 * @author Arvid Halma
 * @version 25-11-2015 - 11:52
 */
public class MatchTest {
    private Tokenizer tokenizer = new MatchingWordTokenizer();
    private StringNormalizer normalizer = StringNormalizers.DEFAULT_STEMMED_I18N;
    private final static int N = 1_000_000;


    @Test
    public void match1(){
        match(true, "Come to The Hague to join the Hub!", "to");
    }

    @Test
    public void match2(){
        match(false, "Come to The Hague to join the Hub!", "arvid");
    }

    @Test
    public void matchAnd1(){
        match(true, "Come to The Hague to join the Hub!", "join & hub");
    }

    @Test
    public void matchAnd2(){
        match(false, "Come to The Hague to join the Hub!", "join & foo");
    }

    @Test
    public void matchOr1(){
        match(true, "Come to The Hague to join the Hub!", "foo | hub");
    }

    @Test
    public void matchOr2(){
        match(false, "Come to The Hague to join the Hub!", "foo | bar");
    }

    @Test
    public void matchNot1(){
        match(true, "Come to The Hague to join the Hub!", "-foo");
    }

    @Test
    public void matchNot2(){
        match(false, "Come to The Hague to join the Hub!", "-hague");
    }

    @Test
    public void matchNot3(){
        match(true, "Come to The Hague to join the Hub!", "-(foo|bar)");
    }

    @Test
    public void matchConcat1(){
        match(true, "Come to The Hague to join the Hub!", "the_hague");
    }

    @Test
    public void matchConcat2(){
        match(false, "Come to The Hague to join the Hub!", "the_foo");
    }

    @Test
    public void matchExact1(){
        match(true, "Come to The Hague to join the Hub!", "\"Hague\"");
    }

    @Test
    public void matchExact2(){
        match(false, "Come to The Hague to join the Hub!", "\"hague\"");
    }

    @Test
    public void matchExactConcat1(){
        match(true, "Come to The Hague to join the Hub!", "\"The\"_\"Hague\"");
    }

    @Test
    public void matchExactConcat2(){
        match(false, "Come to The Hague to join the Hub!", "\"The\"_\"hague\"");
    }

    @Test
    public void matchExactConcat3(){
        match(true, "Come to The Hague to join the Hub!", "\"The\"_\"hague\" | join_the");
    }

    @Test
    public void matchExactWilcards1(){
        match(true, "Come to The Hague to join the Hub!", "JOIN_?_Hub");
    }

    @Test
    public void matchExactWilcards2(){
        match(true, "Come to The Hague to join the Hub!", "to_?_the");
    }

    @Test
    public void matchExactWilcards3(){
        match(true, "Come to The Hague to join the Hub!", "to_*_the");
    }

    @Test
    public void matchExactWilcards4(){
        match(true, "Come to The Hague to join the Hub!", "to_+_the");
    }

    @Test
    public void matchExactWilcards5(){
        match(false, "Come to The Hague to join the Hub!", "come_?_to");
    }

    @Test
    public void matchExactWilcards6(){
        match(true, "Come to The Hague to join the Hub!", "join_*_the");
    }

    @Test
    public void matchExactWilcards7(){
        match(false, "Come to The Hague to join the Hub!", "join_+_the");
    }

    @Test
    public void matchExactUTF8(){
        match(true, "café", "café");
    }

    @Test
    public void matchCombi1(){
        match(true, "Come to The Hague to join the Hub!", "Hello & world OR -(join_the_hub) OR ?_Hague");
    }

    @Test
    public void matchArab1(){
        match(true, "تعال إلى لاهاي للانضمام إلى المحور!", "Hello & world OR لاهاي");
    }


    private void match(boolean expected, String text, String pattern){
        System.out.printf("The text \"%s\" is expected to%s match (%s)\n", text, (expected ? "" : " NOT"), pattern );

        List<Token> textTokens = tokenizer.tokenize(text);
        Token[] tokens = textTokens.toArray(new Token[textTokens.size()]);
        normalizer.normalizeTokens(tokens);
        System.out.println(" - text tokens = " + Arrays.toString(tokens));

        Literal[][] cnf = MatchParser.compileBody(pattern, true, normalizer);
        System.out.println(" - cnf = " + Arrays.deepToString(cnf));
        System.out.println();
        int[] eval = null;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < N; i++) {
            eval = MatchEval.findRange(tokens, new MatchRule(null, cnf), null, null);
        }
        stopWatch.stop();
        System.out.println(" - eval = " + Arrays.toString(eval));
        System.out.println(" - stopWatch: " + stopWatch);
        System.out.println(" - speed (evals/s): " + (int)((double)N/ (stopWatch.getTime()/1000.0)));

        assertEquals(expected, eval != null);
    }
}
