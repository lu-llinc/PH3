package org.c4i.nlp.ph3.match;

import org.c4i.nlp.ph3.tokenize.Token;

import java.util.StringJoiner;

/**
 * CNF to query converter for external (database)systems.
 * @author Arvid Halma
 * @version 28-4-16
 */
public class QueryConverter {
    protected String andOp = " & ";
    protected String orOp = " | ";
    protected String notOp = "not";

    protected boolean ignoreWildcards = true;
    protected boolean sequencesAsConjunction = true;
    protected String quoteBegin = "\"";
    protected String quoteEnd = "\"";

    public String convert(String rule){
        return convert(MatchParser.compileBody(rule, true, null));
    }

    public String convert(Literal[][] cnf) {
        StringJoiner ands = new StringJoiner(andOp);
        for (Literal[] disj : cnf) {
            StringJoiner ors = new StringJoiner(orOp, "(", ")");
            for (Literal literal : disj) {

                StringJoiner concat = sequencesAsConjunction ? new StringJoiner(andOp, quoteBegin, quoteEnd) : new StringJoiner(" ", quoteBegin, quoteEnd);
                for (Token token : literal.tokens) {
                    if (ignoreWildcards && !token.getWord().startsWith("[")) { // skip wildcards
                        concat.add(token);
                    }
                }

                if (literal.isNegated()) {
                    ors.add(notOp + "(" + concat.toString() + ")");
                } else {
                    ors.add(concat.toString());
                }
            }
            ands.add(ors.toString());
        }
        return ands.toString();
    }
}
