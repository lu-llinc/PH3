package org.c4i.nlp.ph3;

import org.c4i.nlp.ph3.match.MatchParser;
import org.c4i.nlp.ph3.match.MatchRuleSet;
import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.c4i.nlp.ph3.normalize.StringNormalizers;
import org.c4i.nlp.ph3.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.SplittingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.Tokenizer;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.c4i.nlp.ph3.match.MatchUtil.textToTokens;
import static org.junit.Assert.assertTrue;

/**
 * @author Arvid Halma
 * @version 14-4-2017 - 20:29
 */
public class RuleTest {

    private Tokenizer tokenizer = new SplittingWordTokenizer();
    private StringNormalizer normalizer = StringNormalizers.LOWER_CASE;
    private final static int N = 1_000_000;


    @Test
    public void matchSimple1(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet("fruit = apple | pear", true, normalizer);
        Map<String, int[]> eval = ruleSet.eval(textToTokens("I like apple juice", normalizer));

        for (Map.Entry<String, int[]> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + Arrays.toString(evalEntry.getValue()));
        }

        assertTrue(eval.containsKey("fruit"));
    }

    @Test
    public void matchSimple2(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet("fruit = apple | pear", true, normalizer);
        Map<String, int[]> eval = ruleSet.eval(textToTokens("I like cocktails", normalizer));

        for (Map.Entry<String, int[]> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + Arrays.toString(evalEntry.getValue()));
        }

        assertTrue(eval.isEmpty());
    }

    @Test
    public void matchSimple3(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "fruit = apple | pear\n" +
                        "drink = milk | beer | cocktail",
                true, normalizer);
        Map<String, int[]> eval = ruleSet.eval(textToTokens("Me like cocktail", normalizer));

        for (Map.Entry<String, int[]> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + Arrays.toString(evalEntry.getValue()));
        }

        assertTrue(eval.containsKey("drink"));
    }

    @Test
    public void matchSimple4(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "fruit = apple | pear\n" +
                        "drink = milk | beer | cocktail\n" +
                        "food = bread | #fruit",

                false, normalizer);
        Map<String, int[]> eval = ruleSet.eval(textToTokens("The monkey eats a pear", normalizer));

        for (Map.Entry<String, int[]> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + Arrays.toString(evalEntry.getValue()));
        }

        assertTrue(eval.containsKey("food"));
    }

}
