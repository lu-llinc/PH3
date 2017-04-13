package org.c4i.nlp.ph3.match;


import org.c4i.nlp.ph3.tokenize.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 * Transform logical expressions to Conjunctive Normal Form (CNF)
 * Every propositional formula can be converted into an equivalent formula that is in CNF.
 * This transformation is based on rules about logical equivalences: the double negative law, De Morgan's laws, and the distributive law.
 * Since all logical formulae can be converted into an equivalent formula in conjunctive normal form, proofs are often based on the assumption that all formulae are CNF.
 * Also evaluating expressions can be done uniformly and efficiently.
 *
 * @author Arvid Halma
 * @version 4/5/13, 9:01 PM
 *
 */
public class CNFTransform {
    private static final String AND = "&", OR  = "|", NOT = "-";

    /**
     * Every propositional formula can be converted into an equivalent formula that is in CNF.
     * @param tree some logical expression AST
     * @return tree (with additional ROOT element)
     */
    public static MatchNode toCNFTree(MatchNode tree) {

        // To CNF general
        // 1. Convert to negation normal form.
        // Move NOTs inwards by repeatedly applying DeMorgan's Law. Specifically,
        // 1.1: replace ~(x | y) with (~x) & (~y); replace  with ; and
        // 1.2: replace ~(x & y) with (~x) | (~y).
        // 1.3: replace ~~x with x.

        tree = nnf(tree);

        // 2. Distribute ORs over ANDs (disjunction over conjunction):
        // A | (B & C) => (A | B) & (A | C).
        // That is: A B C & |
        //      to: A B | A C | &
        // And    : B C & A |
        //      to: B A | C A | &
        tree = distributeDoC(tree);

        return tree;
    }

    private static MatchNode nnf(MatchNode tree){
        tree = nnf11(tree);
        tree = nnf12(tree);
        tree = nnf13(tree);
        return tree;
    }

    private static MatchNode nnf11(MatchNode node){
        // Bubble down negations over OR
        // 1.1: That is: X Y | ~
        //           to: X ~ Y ~ &
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(OR)){
            // reshape tree locally
            MatchNode or = node.left();

            return new MatchNode(AND,
                    nnf11(new MatchNode(NOT, or.left(), null)),
                    nnf11(new MatchNode(NOT, or.right(), null)));
        } else {
            // search children
            return new MatchNode(node.operator, nnf11(node.left()), nnf11(node.right()));
        }
    }

    private static MatchNode nnf12(MatchNode node){
        // Bubble down negations over AND
        // 1.2: That is: X Y & ~
        //           to: X ~ Y ~ |
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(AND)){
            // reshape tree locally
            MatchNode and = node.left();

            return new MatchNode(OR,
                    nnf12(new MatchNode(NOT, and.left(), null)),
                    nnf12(new MatchNode(NOT, and.right(), null)));
        } else {
            // search children
            return new MatchNode(node.operator, nnf12(node.left()), nnf12(node.right()));
        }
    }

    private static MatchNode nnf13(MatchNode node){
        // Remove double negations
        // 1.2: That is: X ~ ~
        //           to: X
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(NOT)){
            // reshape tree locally
            MatchNode not2 = node.left();
            MatchNode x = not2.left();

            // recurse with shortened version of yourself
            return nnf13(new MatchNode(x.operator, x.left(), x.right()));
        } else {
            // search children
            return new MatchNode(node.operator, nnf13(node.left()), nnf13(node.right()));
        }
    }

    private static MatchNode distributeDoC(MatchNode node){
        // 2. Distribute ORs over ANDs (disjunction over conjunction):
        // A | (B & C) => (A | B) & (A | C).
        // That is: A B C & |
        //      to: A B | A C | &
        // And by commutativity :
        //          B C & A |
        //      to: B A | C A | &
        // note: when a,b,c are all equally likely to fail, put 'a' first
        // so all other terms don't need checking anymore.

        if(node == null || node.isLeaf()){
            return node;
        }

        // After applying this rule, an AND has drifted up in the tree.
        // its parent might as well be an OR. Therefore, do head recursion
        node = new MatchNode(node.operator, distributeDoC(node.left()), distributeDoC(node.right()));

        if(node.isOperator(OR) && node.hasChildOperator(AND)){
            // reshape tree locally
            MatchNode a,b,c;
            if(node.left().isOperator(AND)){
                a = node.right();
                b = node.left().left();
                c = node.left().right();
            } else {
                a = node.left();
                b = node.right().left();
                c = node.right().right();
            }

            return new MatchNode(AND,
                    distributeDoC(new MatchNode(OR, a, b)),
                    distributeDoC(new MatchNode(OR, a, c)));
        } else {
            return node;
        }
    }

    public static Literal[][] toCNFArray(MatchNode tree){
        return cnfTreeToCNFArray(toCNFTree(tree));
    }

    /**
     * Convert tree in CNF to nested array of literals.
     * @param cnfTree use toCNFTree() before calling this method
     * @return simle cnf data structure
     */
    public static Literal[][] cnfTreeToCNFArray(MatchNode cnfTree){
        return cnfListToCNFArray(cnfTreeToCNFList(cnfTree));
    }

    public static Literal[][] cnfListToCNFArray(ArrayList<ArrayList<Literal>> cnfList){
        Literal[][] cnf = new Literal[cnfList.size()][];
        for (int i = 0; i < cnfList.size(); i++) {
            ArrayList<Literal> disjList = cnfList.get(i);
            Literal[] disjArray = new Literal[disjList.size()];
            cnf[i] = disjList.toArray(disjArray);
        }
        return cnf;
    }

    /**
     * Convert tree in CNF to nested array of literals.
     * @param cnfTree use toCNFTree() before calling this method
     * @return simle cnf data structure
     */
    public static ArrayList<ArrayList<Literal>> cnfTreeToCNFList(MatchNode cnfTree){
        ArrayList<ArrayList<Literal>> result = new ArrayList<>();
        doConjunction(cnfTree, result);
        return result;
    }


    private static void doConjunction(MatchNode cnfTree, ArrayList<ArrayList<Literal>> result){
        if(cnfTree.isOperator(AND)){
            doConjunction(cnfTree.left(), result);
            doConjunction(cnfTree.right(), result);
        } else {
            // must be disjunction of 1 or more literals/(negated)atoms
            ArrayList<Literal> disjunction = new ArrayList<>();
            doDisjunction(cnfTree, disjunction);
            result.add(disjunction);
        }
    }

    private static void doDisjunction(MatchNode cnfTree, ArrayList<Literal> disjuntion){
        if(cnfTree.isOperator(OR)){
            doDisjunction(cnfTree.left(), disjuntion);
            doDisjunction(cnfTree.right(), disjuntion);
        } else {
            disjuntion.add(doLiteral(cnfTree));
        }
    }

    private static Literal doLiteral(MatchNode node){
        boolean negated = false;
        if(node.isOperator(NOT)){
            negated = true;
            node = node.left();
        }

        if(node.isOperator("A")){
            return new Literal(new Token(node.left().getValue()).setMatchOnNormalized(true), negated);
        } else if(node.isOperator("E")){
            return new Literal(new Token(node.left().getValue()).setMatchOnNormalized(false), negated);
        } else if("?".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if("+".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if("*".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if(node.isOperator("_")){
            Literal left = doLiteral(node.left());
            Literal right = doLiteral(node.right());
            // concat arrays from left and right
            Token[] tokens = Arrays.copyOf(left.tokens, left.tokens.length + right.tokens.length);
            System.arraycopy(right.tokens, 0, tokens, left.tokens.length, right.tokens.length);
            return new Literal(tokens, negated, 'a');
        } else {
            throw new IllegalStateException("Unexpected node in CNF expression: " + node);
        }
    }

    /**
     * Simplify expression
     * <code>
     *     'a' AND (NOT 'b' OR 'c') AND NOT ('b' AND NOT 'a') OR NOT 'b'
     *     = [[-b, a], [-b, -b, c], [-b, -b, a]]
     *     = [[-b, c], [-b, a]]
     * </code>
     *
     * @param cnf expression
     * @return simple cnf data structure
     */
    public static ArrayList<ArrayList<Literal>> simplify(ArrayList<ArrayList<Literal>> cnf){

        // remove duplicates in disjunction
        // a | a = a
        ArrayList<ArrayList<Literal>> tmp = new ArrayList<>();
        for (ArrayList<Literal> disj : cnf) {
            tmp.add(new ArrayList<>(new TreeSet<>(disj)));
        }

        // remove a | -a  and empty disjunctions
        ArrayList<ArrayList<Literal>> tmp2 = new ArrayList<>();
        for (ArrayList<Literal> disj : tmp) {
            ArrayList<Literal> newDisj = new ArrayList<>();
            int d = disj.size();
            for (int i = 0; i < d; i++) {
                Literal litA = disj.get(i);
                if(i + 1 < d && Arrays.equals(litA.tokens, disj.get(i + 1).tokens)) {
                    // must be negations: don't include
                    i++;
                    continue;
                }
                newDisj.add(litA);
            }

            if(!newDisj.isEmpty()){
                tmp2.add(newDisj);
            }
        }

        // remove duplicate conjunctions
        // tmp = new ArrayList<>(new HashSet<>(tmp));
        // done by next step in generalized way...

        // remove duplicate or superset disjunctions
        // a & (a | b)

        //   b | 1 0
        // ----+----
        // a 1 | 1 1
        //   0 | 0 0

        // also:
        // a & (a | b)
        // = (a & a) | (a & b)
        // = a | (a & b)

        //   b | 1 0
        // ----+----
        // a 1 | 1 1
        //   0 | 0 0
        // therefore -> a & (a | b) = a

        int n = tmp2.size();
        tmp = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            ArrayList<Literal> dA = tmp2.get(i);
            boolean isSuperset = false;
            for (int j = 0; j < i; j++) {
                ArrayList<Literal> dB = tmp2.get(j);
                if(dA.containsAll(dB)) {
                    // disjunction B is <= dA
                    isSuperset = true;
                    break;
                }
            }
            if(!isSuperset){
                tmp.add(dA);
            }
        }

        // todo : (a | b) & -b -> a & -b
        // todo : (a & b) | -(a & b) -> []

        return tmp;
    }



}
