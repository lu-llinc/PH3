package org.c4i.nlp.ph3;

import org.apache.commons.lang3.time.StopWatch;
import org.c4i.nlp.ph3.match.MatchParser;
import org.c4i.nlp.ph3.match.MatchRange;
import org.c4i.nlp.ph3.match.MatchRuleSet;
import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.c4i.nlp.ph3.normalize.StringNormalizers;
import org.c4i.nlp.ph3.tokenize.SplittingWordTokenizer;
import org.c4i.nlp.ph3.tokenize.Token;
import org.c4i.nlp.ph3.tokenize.Tokenizer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
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
        Map<String, MatchRange> eval = ruleSet.eval(textToTokens("I like apple juice", normalizer));

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }

        assertTrue(eval.containsKey("fruit") && eval.size() == 1);
    }

    @Test
    public void matchSimple2(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet("fruit = apple | pear", true, normalizer);
        Map<String, MatchRange> eval = ruleSet.eval(textToTokens("I like cocktails", normalizer));

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }

        assertTrue(eval.isEmpty());
    }

    @Test
    public void matchSimple3(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "fruit = apple | pear\n" +
                        "drink = milk | beer | cocktail",
                true, normalizer);
        Map<String, MatchRange> eval = ruleSet.eval(textToTokens("Me like cocktail", normalizer));

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }

        assertTrue(eval.containsKey("drink") && eval.size() == 1);
    }

    @Test
    public void matchSimple4(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "fruit = apple | pear\n" +
                        "drink = milk | beer | cocktail\n" +
                        "food = bread | #fruit",

                false, normalizer);
        Map<String, MatchRange> eval = ruleSet.eval(textToTokens("The monkey eats a pear", normalizer));

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }

        assertTrue(eval.containsKey("food") && eval.containsKey("fruit"));
    }

    @Test
    public void matchSimple4n(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "fruit = apple | pear\n" +
                        "animal = bear & -beer OR monkey\n" +
                        "drink = milk | beer | cocktail\n" +
                        "food = bread | #fruit",

                false, normalizer);

        Map<String, MatchRange> eval = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String text = "The monkey eats a pear";
        for (int i = 0; i < N; i++) {
            List<Token> tokens = tokenizer.tokenize(text);
            eval = ruleSet.eval(normalizer.normalizeTokens(tokens).toArray(new Token[tokens.size()]));
        }
        stopWatch.stop();

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }
        System.out.println("stopWatch = " + stopWatch);
        System.out.println(ruleSet.highlight(text, eval));
        assertTrue(eval.containsKey("food"));
    }

    @Test
    public void matchSimple4nMarkup(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "// LANG = [ar,en], tokenize=word\n" +
                        "// comment\n" +
                        "\n" +
                        "fruit = apple | pear\n" +
                        "     drink = milk | beer | cocktail\n" +
                        "food = bread | #fruit\n" +
                        "food1 = bread | #fruit\n" +
                        "food2 = bread | #fruit\n" +
                        "food3 = bread | #fruit\n" +
                        "food4 = bread | #fruit\n" +
                        "food5 = bread | #fruit\n" +
                        "food6 = bread | #fruit\n" +
                        "food7 = bread | #fruit\n",

                false, normalizer);

        Map<String, MatchRange> eval = null;
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        List<Token> tokens = tokenizer.tokenize("The monkey eats a pear");
        tokens = normalizer.normalizeTokens(tokens);
        Token[] tokens1 = tokens.toArray(new Token[tokens.size()]);
        for (int i = 0; i < N; i++) {
            eval = ruleSet.eval(tokens1);
        }
        stopWatch.stop();

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }
        System.out.println("stopWatch = " + stopWatch);
        assertTrue(eval.containsKey("food"));
    }

    @Test
    public void matchSimple4nMarkupPar(){
        MatchRuleSet ruleSet = MatchParser.compileRuleSet(
                "// LANG = [ar,en], tokenize=word\n" +
                        "// comment\n" +
                        "\n" +
                        "fruit = apple | pear\n" +
                        "     drink = milk | beer | cocktail\n" +
                        "food = bread | #fruit\n" +
                        "food1 = bread | #fruit\n" +
                        "food2 = bread | #fruit\n" +
                        "food3 = bread | #fruit\n" +
                        "food4 = bread | #fruit\n" +
                        "food5 = bread | #fruit\n" +
                        "food6 = bread | #fruit\n" +
                        "food7 = bread | #fruit\n",

                false, normalizer);

        Map<String, MatchRange> eval = null;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Token> tokens = tokenizer.tokenize("The monkey eats a pear");
        tokens = normalizer.normalizeTokens(tokens);
        Token[] tokens1 = tokens.toArray(new Token[tokens.size()]);
        for (int i = 0; i < N; i++) {
            eval = ruleSet.evalParallel(tokens1);
        }
        stopWatch.stop();

        for (Map.Entry<String, MatchRange> evalEntry : eval.entrySet()) {
            System.out.println(evalEntry.getKey() + " @ " + evalEntry.getValue());
        }
        System.out.println("stopWatch = " + stopWatch);
        assertTrue(eval.containsKey("food"));
    }

}
