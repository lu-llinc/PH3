package org.c4i.nlp.ph3.match;

import org.c4i.nlp.ph3.tokenize.Token;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A literal is an atomic formula (in this case a sequence of tokens) or its negation.
 * The definition mostly appears in proof theory (of classical logic), e.g. in conjunctive normal form.
 * It just happens to be used for that here as well.
 * <p>
 * This literal is just a container for a single tokens.
 * Evaluation is done elsewhere. In this way, serialization and expression transformations can be
 * done independently from the underlying semantics.
 *
 * @author Arvid Halma
 * @version 27-4-2016 - 20:51
 */
public class Literal implements Comparable<Literal>{
    Token[] tokens;
    boolean negated;
    char meta;

    public Literal(Token[] tokens, boolean negated, char meta) {
        this.tokens = tokens;
        this.negated = negated;
        this.meta = meta;
    }

    public Literal(Token token, boolean negated, char meta) {
        this(new Token[]{token}, negated, meta);
    }

    public Literal(Token token, char meta) {
        this(new Token[]{token}, false, meta);
    }

    public Literal(Token token, boolean negated) {
        this(new Token[]{token}, negated, 'a');
    }

    public Literal(Token token) {
        this(new Token[]{token}, false, 'a');
    }

    public Token[] getTokens() {
        return tokens;
    }

    public Literal setTokens(Token[] tokens) {
        this.tokens = tokens;
        return this;
    }

    public boolean isNegated() {
        return negated;
    }

    public Literal setNegated(boolean negated) {
        this.negated = negated;
        return this;
    }

    public char getMeta() {
        return meta;
    }

    public Literal setMeta(char meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;

        Literal literal = (Literal) o;

        if (negated != literal.negated) return false;
        if (meta != literal.meta) return false;
        return Arrays.equals(tokens, literal.tokens);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(tokens);
        result = 31 * result + (negated ? 1 : 0);
        result = 31 * result + (int) meta;
        return result;
    }

    @Override
    public String toString() {
        String tokens = Arrays.stream(this.tokens).map(Token::toString).collect(Collectors.joining("_"));
        if(meta != 'a'){
            tokens = meta+tokens;
        }
        if(negated){
            tokens = "-"+tokens;
        }
        return tokens;
    }

    @Override
    public int compareTo(Literal lit) {

        for (int i = 0; i < tokens.length && i < lit.tokens.length ; i++) {
            Token token = tokens[i];

            int tokenComp = token.compareTo(lit.tokens[i]);
            if(tokenComp == 0) {
                if (negated && !lit.negated) {
                    return -1;
                } else if(!negated && lit.negated){
                    return 1;
                }
            } else {
                return tokenComp;
            }
        }
        // equals so far...
        return Integer.compare(tokens.length, lit.tokens.length);
    }
}
