package org.c4i.nlp.ph3.match;

import java.util.Arrays;

/**
 * A trigger rule: when the expression (body) matches, the label (head) is triggered.
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class MatchRule {

    String head;
    Literal[][] expression;

    public MatchRule(String head, Literal[][] expression) {
        this.head = head;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return head + " = " + Arrays.toString(expression);
    }
}
