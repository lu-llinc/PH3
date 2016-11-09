package org.c4i.nlp.ph3.tokenize;


import org.c4i.nlp.ph3.normalize.StringNormalizer;

/**
 * Word-like part of a text.
 * @author Arvid Halma
 * @version 27-9-2015 - 13:29
 */
public class Token implements Comparable<Token>, CharSequence{

    public static final Token ANY_ONE = new Token("?");
    public static final Token ANY_ZERO_OR_MORE = new Token("*");
    public static final Token ANY_ONE_OR_MORE = new Token("+");

    private String word;
    private String normalizedWord;
    private int location;
    private String tag;
    private String origin;
    private double weight;
    private boolean matchOnNormalized = true;

    public Token(String word) {
        this.word = word;
        this.normalizedWord = word;
        this.weight = 1.0;
        this.tag = null;
    }

    public Token(String word, int location) {
        this.word = word;
        this.normalizedWord = word;
        this.location = location;
        this.weight = 1.0;
        this.tag = null;
    }

    public Token(String word, String tag, String origin, double weight) {
        this.word = word;
        this.tag = tag;
        this.origin = origin;
        this.weight = weight;
    }

    public Token(String word, String normalizedWord, int location, String tag, String origin, double weight) {
        this.word = word;
        this.normalizedWord = normalizedWord;
        this.location = location;
        this.tag = tag;
        this.origin = origin;
        this.weight = weight;
    }

    public Token(String word, String normalizedWord, int location, String tag, String origin, double weight, boolean matchOnNormalized) {
        this.word = word;
        this.normalizedWord = normalizedWord;
        this.location = location;
        this.tag = tag;
        this.origin = origin;
        this.weight = weight;
        this.matchOnNormalized = matchOnNormalized;
    }

    public String getWord() {
        return word;
    }

    public Token setWord(String word) {
        this.word = word;
        return this;
    }

    public String getNormalizedWord() {
        return normalizedWord;
    }

    public Token setNormalizedWord(String normalizedWord) {
        this.normalizedWord = normalizedWord;
        return this;
    }

    public int getLocation() {
        return location;
    }

    public Token setLocation(int location) {
        this.location = location;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public Token setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public Token setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public Token setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public boolean isMatchOnNormalized() {
        return matchOnNormalized;
    }

    public Token setMatchOnNormalized(boolean matchOnNormalized) {
        this.matchOnNormalized = matchOnNormalized;
        return this;
    }

    public Token normalize(StringNormalizer normalizer){
        this.normalizedWord = normalizer.normalize(this.word);
        return this;
    }

    public boolean isNormalized(){
        return normalizedWord != null;
    }

    @Override
    public int compareTo(Token t) {
        return word == null ? 0 : word.compareTo(t.word);
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;

        Token token = (Token) o;

        if(token == ANY_ONE){
            return true;
        }

        String w1, w2;
        if(token.matchOnNormalized) {
            w1 = getNormalizedWord();
            w2 = token.getNormalizedWord();
        } else {
            w1 = getWord();
            w2 = token.getWord();
        }

        return !(w1 != null ? !w1.equals(w2) : w2 != null);
    }

    @Override
    public int hashCode() {
        String w = matchOnNormalized ? getNormalizedWord() : getWord();
        return w != null ? w.hashCode() : 0;
    }

    @Override
    public String toString() {
        return matchOnNormalized ? getNormalizedWord() : getWord();
    }

    public String toExtendedString() {
        if(weight == 1.0){
            String s;
            if(tag == null){
                s = getNormalizedWord();
            } else {
                s = String.format("%s/%s", getNormalizedWord(), tag);
            }

            if(origin != null){
                s += ":" + origin;
            }

            return s;
        }
        return String.format("%s/%s(%.2f):%s", normalizedWord, tag, weight, origin == null ? "" : origin);
    }
}
