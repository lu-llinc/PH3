package org.c4i.nlp.ph3.match;

import org.c4i.nlp.ph3.tokenize.Token;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A list of rules that define tags that may be triggered, given their corresponding expressions.
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class MatchRuleSet {

    final Map<String, MatchRule> rules;

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
            Arrays.stream(rule.expression).flatMap(Arrays::stream).forEach(lit -> {
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
     * Reurn all matching labels
     * @param tokens
     * @return
     */
    public Map<String, MatchRange> eval(Token[] tokens){
        final Map<String, MatchRange> result = new ConcurrentHashMap<>();
        rules.values().forEach(rule -> {
            int[] range = MatchEval.findRange(tokens, rule, this, result);
            if(range != null){
                result.put(rule.head, new MatchRange(rule.head, range[0], range[1], tokens[range[0]].getCharStart(), tokens[range[1]-1].getCharEnd()));
            }
        });
        return result;
    }

    /**
     * Reurn all matching labels
     * @param tokens
     * @return
     */
    public Map<String, MatchRange> evalParallel(final Token[] tokens){
        final Map<String, MatchRange> result = new ConcurrentHashMap<>();
        rules.values().stream().parallel().forEach(rule -> {
            int[] range = MatchEval.findRange(tokens, rule, this, result);
            if(range != null){
                result.put(rule.head, new MatchRange(rule.head, range[0], range[1], tokens[range[0]].getCharStart(), tokens[range[1]-1].getCharEnd()));
            }
        });
        return result;
    }



    public static String highlight(String orgText, Map<String, MatchRange> eval){
        StringBuilder sb = new StringBuilder();
        Collection<MatchRange> ranges = eval.values();
        for (int i = 0; i < orgText.length(); i++) {
            for (MatchRange range : ranges) {
                if(range.charStart == i){
                    sb.append("<span class=\"match ").append(range.label).append("\">");
                }
                if(range.charEnd == i){
                    sb.append("</span>");
                }
            }
            char c = orgText.charAt(i);
            if(c == '\n'){
                sb.append("<br/>");
            }
            sb.append(c);
        }

        for (MatchRange range : ranges) {
            if(range.charEnd == orgText.length()){
                sb.append("</span>");
            }
        }

        return sb.toString();
    }




    @Override
    public String toString() {
        return rules.values().stream().map(MatchRule::toString).collect(Collectors.joining("\n"));
    }
}
