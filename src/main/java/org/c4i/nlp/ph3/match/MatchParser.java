package org.c4i.nlp.ph3.match;



import org.c4i.nlp.ph3.normalize.StringNormalizer;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ToStringFormatter;
import org.parboiled.support.Var;
import org.parboiled.trees.GraphNode;

import java.util.*;
import java.util.stream.Collectors;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.parboiled.trees.GraphUtils.printTree;

/**
 * Compiles a pattern into a lowel level form. Conjunctive Normal Form.
 *
 * @author Arvid Halma
 * @version 23-11-2015
 */
@SuppressWarnings("WeakerAccess")
@BuildParseTree
public class MatchParser extends BaseParser<MatchNode> {



    public static MatchRuleSet compileRuleSet(String rules, boolean simplify, StringNormalizer normalizer){

        List<MatchRule> ruleList = Arrays.stream(rules.split("\n"))
                .map(String::trim)
                .filter(s -> !s.startsWith("//") && !s.isEmpty())
                .map(s -> compileRule(s, simplify, normalizer)).collect(Collectors.toList());

        return new MatchRuleSet(ruleList);

    }

    public static MatchRule compileRule(String rule, boolean simplify, StringNormalizer normalizer){
        String[] split = rule.split("=", 1);
        if (split.length < 2){
            throw new IllegalArgumentException("The rule does not assign a rule name (rule_name = ...)");
        }
        return new MatchRule(split[0].trim(), compileBody(split[1].trim(), simplify, normalizer));
    }

    public static Literal[][] compileBody(String expression){
        return compileBody(expression, true, null);
    }

    public static Literal[][] compileBody(String expression, boolean simplify, StringNormalizer normalizer){
        MatchParser parser = Parboiled.createParser(MatchParser.class);
        ParsingResult<?> result = new RecoveringParseRunner(parser.ExpressionLine()).run(expression);
        if (result.hasErrors()) {
            throw new IllegalArgumentException(printParseErrors(result));
        }

        Object ast = result.parseTreeRoot.getValue();
        MatchNode cnfTree = CNFTransform.toCNFTree((MatchNode) ast);
        ArrayList<ArrayList<Literal>> cnfList = CNFTransform.cnfTreeToCNFList(cnfTree);
        if(simplify){
            cnfList = CNFTransform.simplify(cnfList);
        }

        if(normalizer != null){
            cnfList.forEach(disj -> disj.forEach(lit -> normalizer.normalizeTokens(lit.getTokens())));
        }
        return CNFTransform.cnfListToCNFArray(cnfList);
    }

    public Rule RuleLine() {
        return Sequence(Expression(), EOI);
    }

    public Rule ExpressionLine() {
        return Sequence(Expression(), EOI);
    }

    Rule Expression() {
        Var<String> op = new Var<>();
        return Sequence(
                And(),
                ZeroOrMore(
                        // we use a FirstOf(String, String) instead of a AnyOf(String) so we can use the
                        // fromStringLiteral transformation (see below), which automatically consumes trailing whitespace
                        FirstOf("| ", "OR "), op.set(matchOrDefault("|")),
                        And(),
                        push(new MatchNode("|", pop(1), pop()))
                )
        );
    }

    Rule And() {
        Var<String> op = new Var<>();
        return Sequence(
                Concat(),
                ZeroOrMore(
                        FirstOf("& ", "AND "), op.set(matchOrDefault(("&"))),
                        Concat(),
                        push(new MatchNode("&", pop(1), pop()))
                )
        );
    }

    Rule Concat() {
        Var<String> op = new Var<>();
        return Sequence(
                Atom(),
                ZeroOrMore(
                        "_ ", op.set(matchOrDefault("_")),
                        Atom(),
                        push(new MatchNode("_", pop(1), pop()))
                )
        );
    }

    Rule Atom() {
//        return FirstOf(DoubleQuotedString(), SingleQuotedString(), UnquotedQuotedString(), SquareRoot(), Parens());
        return FirstOf(DoubleQuotedString(), SingleQuotedString(), AnyOne(), AnyOneOrMore(), AnyZeroOrMore(), Not(), LookUp(), Parens(), UnquotedQuotedString());
    }

    /*MatchRule SquareRoot() {
        return Sequence(
                "SQRT ",
                Parens(),

                // create a new AST node with a special operator "R" and only one child
                push(new MatchNode("R", pop(), null))
        );
    }*/

    Rule Not() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf("-", "NOT "), op.set(matchOrDefault("-")),
                Atom(),

                push(new MatchNode("-", pop(), null))
        );
    }

    Rule LookUp() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf("#", "LOOKUP "), op.set(matchOrDefault("#")),
                UnquotedQuotedString(),

                push(new MatchNode("#", pop(), null))
        );
    }

    Rule Parens() {
        return Sequence("( ", Expression(), ") ");
    }

    Rule AnyOne() {
        return Sequence("?", push(new MatchNode("?")), WhiteSpace());
    }

    Rule AnyOneOrMore() {
        return Sequence("+", push(new MatchNode("+")), WhiteSpace());
    }

    Rule AnyZeroOrMore() {
        return Sequence("*", push(new MatchNode("*")), WhiteSpace());
    }

    /*MatchRule Number() {
        return Sequence(
                // we use another Sequence in the "Number" Sequence so we can easily access the input text matched
                // by the three enclosed rules with "match()" or "matchOrDefault()"
                Sequence(
                        Optional("-"),
                        OneOrMore(Digit()),
                        Optional(".", OneOrMore(Digit()))
                ),

                // the matchOrDefault() call returns the matched input text of the immediately preceding rule
                // or a default string (in this case if it is run during error recovery (resynchronization))
                push(new MatchNode(Double.parseDouble(matchOrDefault("0")))),
                WhiteSpace()
        );
    }*/

    /*MatchRule Digit() {
        return CharRange("0", "9");
    }*/

    Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f").label("Whitespace"));
    }

    Rule QuotedStrNormal(String escapeSeq) {
        return NoneOf(escapeSeq);
    }

    Rule QuotedStrSpecial(String escapeSeq) {
        return String(escapeSeq);
    }

    Rule QuotedStrNSN(String escapeSeq) {
        return Sequence(Sequence(
                ZeroOrMore(QuotedStrNormal(escapeSeq)),
                ZeroOrMore(QuotedStrSpecial(escapeSeq), ZeroOrMore(QuotedStrNormal(escapeSeq)))
        ), push(new MatchNode(matchOrDefault(""))));
    }

    Rule SingleQuotedString() {
        return Sequence('\'', QuotedStrNSN("\\\'"), '\'', WhiteSpace(), push(new MatchNode("A", pop(), null)));
    }

    Rule DoubleQuotedString() {
        return Sequence('"', QuotedStrNSN("\\\""), '"', WhiteSpace(), push(new MatchNode("E", pop(), null)));
    }

    Rule UnquotedQuotedString() {
        return Sequence(
                OneOrMore(FirstOf(
                        CharRange('A','Z'), // upper case
                        CharRange('a','z'), // lower case
                        CharRange('0','9'), // digits
                        CharRange('\u00c0','\u00ff') // Latin chars with accents
                )),
                push(new MatchNode("A", new MatchNode(matchOrDefault("")), null)), WhiteSpace());
    }


    // we redefine the rule creation for string literals to automatically match trailing whitespace if the string
    // literal ends with a space character, this way we don't have to insert extra whitespace() rules after each
    // character or string literal

    @Override
    protected Rule fromStringLiteral(String string) {
        return string.endsWith(" ") ?
                Sequence(String(string.substring(0, string.length() - 1)), WhiteSpace()) :
                String(string);
    }


    //**************** Interactive mode ****************
    public static void main(String[] args) {
        MatchParser parser = Parboiled.createParser(MatchParser.class);

        while (true) {
            System.out.print("Enter a match expression (single RETURN to exit)!\n");
            String input = new Scanner(System.in).nextLine();
            if (StringUtils.isEmpty(input)) break;

            ParsingResult<?> result = new RecoveringParseRunner(parser.ExpressionLine()).run(input);

            if (result.hasErrors()) {
                System.out.println("\nParse Errors:\n" + printParseErrors(result));
            } else {
                Object value = result.parseTreeRoot.getValue();
                if (value != null) {
                    String str = value.toString();
                    int ix = str.indexOf('|');
                    if (ix >= 0) str = str.substring(ix + 2); // extract value part of AST node toString()
                    System.out.println(input + " = " + str + '\n');
                }
                if (value instanceof GraphNode) {
                    System.out.println("\nAbstract Syntax Tree:\n" +
                            printTree((GraphNode) value, new ToStringFormatter(null)) + '\n');

                    MatchNode cnfTree = CNFTransform.toCNFTree((MatchNode) value);
                    System.out.println("CNF Abstract Syntax Tree:\n" +
                            printTree(cnfTree, new ToStringFormatter(null)) + '\n');

                    ArrayList<ArrayList<Literal>> cnfList = CNFTransform.cnfTreeToCNFList(cnfTree);
                    System.out.println("CNF[][]        = " + cnfList);
                    cnfList = CNFTransform.simplify(cnfList);
                    System.out.println("CNF[][] simple = " + cnfList);

                    Literal[][] cnfArray = CNFTransform.cnfListToCNFArray(cnfList);
                    String luceneQuery = new LuceneConverter().convert(cnfArray);
                    System.out.println("Lucene = " + luceneQuery);
                    String postgreSQL = new PostgresConverter().convert(cnfArray);
                    System.out.println("PostgreSQL = " + postgreSQL);

                } else {
                    System.out.println("\nParse Tree:\n" + printNodeTree(result) + "\n");
                }
            }
        }
    }

}