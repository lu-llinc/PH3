package org.c4i.nlp.ph3.match;

import org.parboiled.trees.ImmutableBinaryTreeNode;

import java.util.Objects;

/**
 * The AST node for the calculators. The type of the node is carried as a Character that can either contain
 * an operator char or be null. In the latter case the AST node is a leaf directly containing a value.
 */
public class MatchNode extends ImmutableBinaryTreeNode<MatchNode> {
    String value;
    String operator;

    public MatchNode(String value) {
        super(null, null);
        this.value = value;
    }

    public MatchNode(String operator, MatchNode left, MatchNode right) {
        super(left, right);
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isLeaf(){
        return operator == null;
    }

    public boolean isOperator(String op){
        return Objects.equals(operator, op);
    }

    public boolean leftIsOperator(String op){
        MatchNode left = left();
        return left != null && Objects.equals(left.operator, op);
    }

    public boolean rightIsOperator(String op){
        MatchNode right = right();
        return right != null && Objects.equals(right.operator, op);
    }

    public boolean hasChildOperator(String op){
        return leftIsOperator(op) || rightIsOperator(op);
    }

    public String eval() {
        if (operator == null) return value;
        switch (operator) {
            case "_":
                return left().eval() + "_" + right().eval();
            case "&":
                return "(" + left().eval() + " AND " + right().eval() + ")";
            case "|":
                return "(" + left().eval() + " OR " + right().eval() + ")";
            case "E":
                return "\"" + left().eval() +"\"";
            case "A":
                return "'" + (left().eval()) + "'";
            case "-":
                return "NOT(" + left().eval() + ")";
            case "?": return "?";
            case "+": return "+";
            case "*": return "*";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return (isLeaf() ? "Value " + value : "Operator '" + operator + "'") + " : " + eval();
    }
}
