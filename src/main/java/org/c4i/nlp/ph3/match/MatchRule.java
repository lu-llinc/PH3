package org.c4i.nlp.ph3.match;

import java.util.Arrays;

/**
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class MatchRule {

    String head;
    Literal[][] body;

    public MatchRule(String head, Literal[][] body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public String toString() {
        return head + " = " + Arrays.toString(body);
    }
}
