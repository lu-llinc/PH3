package org.c4i.nlp.ph3.match;

import org.c4i.nlp.ph3.tokenize.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A list of rules that define tags that may be triggered, given their corresponding expressions.
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class MatchRuleSet {

    Map<String, MatchRule> rules;

    public MatchRuleSet() {
        rules = new HashMap<>();
    }

    public MatchRuleSet(Map<String, MatchRule> rules) {
        this.rules = rules;
        checkLookups();
    }

    public MatchRuleSet(List<MatchRule> rules) {
        this();
        for (MatchRule rule : rules) {
            if(this.rules.containsKey(rule.head)){
                throw new IllegalArgumentException(String.format("There are multiple definitions of rule '%s'.", rule.head));
            } else {
                this.rules.put(rule.head, rule);
            }
        }
        checkLookups();
    }

    private void checkLookups(){
        Set<String> heads = rules.keySet();
        for (MatchRule rule : rules.values()) {
            Arrays.stream(rule.body).flatMap(Arrays::stream).forEach(lit -> {
                String lookup = lit.tokens[0].getWord();
                if(lit.meta == '#') {
                    if (!heads.contains(lookup)) {
                        throw new IllegalArgumentException(String.format("Rule '%s' contains a lookup to a rule that is not defined: #%s.", rule.head, lookup));
                    } else if (rule.head.equals(lookup)) {
                        throw new IllegalArgumentException(String.format("Rule '%s' contains a lookup to itself. No recursion allowed.", lookup));
                    }
                }
            });
        }
    }

    /**
     * Reurn all matching heads
     * @param tokens
     * @return
     */
    public Map<String, int[]> eval(Token[] tokens){
        Map<String, int[]> result = new HashMap<>();
        for (MatchRule rule : rules.values()) {
            int[] range = MatchEval.findRange(tokens, rule.body, this);
            if(range != null){
                result.put(rule.head, range);
            }
        }
        return result;
    }


    @Override
    public String toString() {
        return rules.values().stream().map(MatchRule::toString).collect(Collectors.joining("\n"));
    }
}
