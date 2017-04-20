package org.c4i.nlp.ph3.match;

/**
 * @author Arvid Halma
 * @version 20-4-2017 - 13:35
 */
public class MatchRange {
    String label;
    int tokenStart, tokenEnd;
    int charStart, charEnd;

    public MatchRange(String label, int tokenStart, int tokenEnd, int charStart, int charEnd) {
        this.label = label;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
        this.charStart = charStart;
        this.charEnd = charEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchRange)) return false;

        MatchRange that = (MatchRange) o;

        if (tokenStart != that.tokenStart) return false;
        if (tokenEnd != that.tokenEnd) return false;
        if (charStart != that.charStart) return false;
        if (charEnd != that.charEnd) return false;
        return label != null ? label.equals(that.label) : that.label == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + tokenStart;
        result = 31 * result + tokenEnd;
        result = 31 * result + charStart;
        result = 31 * result + charEnd;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MatchRange{");
        sb.append("label='").append(label).append('\'');
        sb.append(", tokenStart=").append(tokenStart);
        sb.append(", tokenEnd=").append(tokenEnd);
        sb.append(", charStart=").append(charStart);
        sb.append(", charEnd=").append(charEnd);
        sb.append('}');
        return sb.toString();
    }
}
