package org.c4i.nlp.ph3.tokenize;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convenience methods fo dealing with Tokens
 * @author Arvid Halma
 * @version 15-10-15.
 */
public class TokenUtil {

    private TokenUtil(){}

    public static String toSentence(List<Token> tokens){
        return tokens.stream().map(Token::getWord).collect(Collectors.joining(" "));
    }

    public static String toAnnotatedSentence(List<Token> tokens){
        return tokens.stream().map(Token::toString).collect(Collectors.joining(" "));
    }

    public static List<String> toWordList(Stream<Token> tokens, boolean normalize){
        return tokens.map(t -> normalize ? t.getWord() : t.getNormalizedWord()).collect(Collectors.toList());
    }

    public static List<String> toWordList(Collection<Token> tokens, boolean normalize){
        return toWordList(tokens.stream(), normalize);
    }

    public static List<Token> toTokenList(List<String> words){
        List<Token> result = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            result.add(new Token(word, i));
        }
        return result;
    }

    public static String[] toWordArray(List<Token> tokens, boolean normalize){
        return toWordList(tokens, normalize).toArray(new String[tokens.size()]);
    }

    public static List<Token> tokenCombis(List<Token> tokens, int k, String separator){
        List<Token> combis = new ArrayList<>();
        int n = tokens.size();
        for (int i = 0; i <= n - k; i++) {
            StringBuilder combi = new StringBuilder(tokens.get(i).getWord());
            for (int j = 1; j < k; j++) {
                combi.append(separator).append(tokens.get(i + j));
            }
            combis.add(new Token(combi.toString()));
        }
        return combis;
    }

    public static List<String> wordCombis(List<String> words, int k, String separator){
        List<String> combis = new ArrayList<>();
        int n = words.size();
        for (int i = 0; i <= n - k; i++) {
            StringBuilder combi = new StringBuilder(words.get(i));
            for (int j = 1; j < k; j++) {
                combi.append(separator).append(words.get(i + j));
            }
            combis.add(combi.toString());
        }
        return combis;
    }

    public static List<String> wordCombisAll(List<String> tokens, int k, String separator){
        List<String> combis = new ArrayList<>();
        int n = tokens.size();
        for (int i = 0; i < n; i++) {
            StringBuilder combi = new StringBuilder(tokens.get(i));
            for (int j = 1; j < k && i + j < n; j++) {
                combi.append(separator).append(tokens.get(i + j));
                combis.add(combi.toString());
            }
        }
        return combis;
    }

    public static List<Token> tokenCombisAll(List<Token> tokens, int k, String separator){
        List<Token> combis = new ArrayList<>();
        int n = tokens.size();
        for (int i = 0; i < n; i++) {
            StringBuilder combi = new StringBuilder(tokens.get(i).getWord());
            for (int j = 1; j < k && i + j < n; j++) {
                combi.append(separator).append(tokens.get(i + j));
                combis.add(new Token(combi.toString()));
            }
        }
        return combis;
    }

    public static boolean contains(List<Token> sentence, List<Token> pattern){
        return find(sentence, pattern) >= 0;
    }

    public static int find(List<Token> sentence, List<Token> pattern){
        int[] match = findRange(sentence, pattern);
        return match == null ? -1 : match[0];
    }

    public static int[] findRange(Token[] sentence, Token[] pattern){
        return findRange(Arrays.asList(sentence), Arrays.asList(pattern));
    }

    public static int[] findRange(List<Token> sentence, List<Token> pattern){
        final int S = sentence.size();
        final int P = pattern.size();

        int matchStart = Integer.MAX_VALUE;
        int matchEnd = -1;

        nextS : for (int si = 0; si < S; si++) {
            int pi;

            nextP: for (pi = 0; pi < P; pi++) {

                Token p = pattern.get(pi);
                if(si + pi >= S){
                    return null;
                }
                Token s = sentence.get(si + pi);

                if (p == Token.ANY_ONE) {
                    matchStart = Math.min(si, matchStart);
                    matchEnd = Math.max(si + pi + 1, matchEnd);
                    continue nextP;
                } else  if (p == Token.ANY_ZERO_OR_MORE || p == Token.ANY_ONE_OR_MORE) {
                    pi++;
                    if(pi == P){
                        matchStart = Math.min(si, matchStart);
                        matchEnd = Math.max(si + pi, matchEnd);
                        return new int[]{matchStart, matchEnd}; // last pattern token
                    }

                    Token pQuant = p;
                    p = pattern.get(pi);  // next p
                    if(p == Token.ANY_ONE || p == Token.ANY_ONE_OR_MORE || p == Token.ANY_ZERO_OR_MORE){
                        throw new IllegalArgumentException("Pattern contains multiple wildcards next to each other: " + pattern);
                    }
                    if(pi + si == S){
                        return null; // no more tokens in sentence, but pattern expects token
                    }

                    // find first match for p
                    for (int k = si + pi - (pQuant == Token.ANY_ZERO_OR_MORE ? 1 : 0); k < S; k++) {
                        if(sentence.get(k).equals(p)){
                            si = k;
                            matchEnd = si + 1;
                            continue nextP;
                        }
                    }
                    matchStart = Integer.MAX_VALUE;
                    matchEnd = -1;
                    continue nextS; // next p not found
                }

                // literal check
                if (!s.equals(p)) {
                    matchStart = Integer.MAX_VALUE;
                    matchEnd = -1;
                    continue nextS;
                } else {
                    matchStart = Math.min(si, matchStart);
                    matchEnd = Math.max(si + pi + 1, matchEnd);
                }
            }

            if(pi == P){
                // full pattern matched
                return new int[]{matchStart, matchEnd};
            }
        }
        return null;
    }


}
