package org.c4i.nlp.ph3.match;

/**
 * Convert matching rule to PostgreSQL text search queries.
 * see: http://www.postgresql.org/docs/9.2/static/datatype-textsearch.html
 * @author Arvid Halma
 * @version 28-4-16
 */
public class PostgresConverter extends QueryConverter {
    public PostgresConverter() {
        andOp = " & ";
        orOp = " & ";
        notOp = "!";

        // Real juxtaposition is not supported, and a words sequence is converted to conjunction anyway.
        // Double quoting is not the same (singe token, vs. separate ones)
        // http://www.postgresql.org/docs/9.1/static/textsearch-controls.html
        sequencesAsConjunction = true;

        quoteBegin = quoteEnd = "";

        ignoreWildcards = true;
    }

}
