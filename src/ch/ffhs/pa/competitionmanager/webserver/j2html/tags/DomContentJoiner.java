package ch.ffhs.pa.competitionmanager.webserver.j2html.tags;

public class DomContentJoiner {

    public static UnescapedText join(CharSequence delimiter, boolean fixPeriodAndCommaSpacing, Object... stringOrDomObjects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : stringOrDomObjects) {
            if (o instanceof String) {
                sb.append(((String) o).trim()).append(delimiter);
            } else if (o instanceof DomContent) {
                sb.append(((DomContent) o).render().trim()).append(delimiter);
            } else if (o == null) {
                //Discard null objects so iff/iffelse can be used with join
            } else {
                throw new RuntimeException("You can only join DomContent and String objects");
            }
        }
        String joined = sb.toString().trim();
        if (fixPeriodAndCommaSpacing) {
            joined = joined.replaceAll("\\s\\.", ".");
            joined = joined.replaceAll("\\s\\,", ",");
        }
        return new UnescapedText(joined);
    }

}
