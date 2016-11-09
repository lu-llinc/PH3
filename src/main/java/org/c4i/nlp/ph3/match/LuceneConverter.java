package org.c4i.nlp.ph3.match;

/**
 * Convert matching rule to Apache Lucene.
 * @author Arvid Halma
 * @version 28-4-16
 */
public class LuceneConverter extends QueryConverter{

    public LuceneConverter() {
        andOp = " AND ";
        orOp = " OR ";

        // Note: The NOT operator cannot be used with just one term. For example, the following search will return no results:
        // https://lucene.apache.org/core/2_9_4/queryparsersyntax.html#Wildcard Searches
        notOp = "-";

        sequencesAsConjunction = false;
        quoteBegin = "+\"";
        quoteEnd = "\"";
        ignoreWildcards = true;
    }
}
