package ch.ffhs.pa.competitionmanager.utils;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to convert between date and localized date strings.
 */
public class DateStringConverter {

    private DateFormat dateFormat;
    private FieldPosition yearPosition;

    /**
     * Constructur
     * @param locale The Locale
     */
    public DateStringConverter(Locale locale) {
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        yearPosition = new FieldPosition(DateFormat.YEAR_FIELD);

        // Alternative...
        // https://dotnetcodr.com/2015/01/17/localising-dates-in-java-8-using-datetimeformatter/
        // https://www.baeldung.com/java-datetimeformatter
        // DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
    }

    /**
     * Convert date to string
     * @param date The date
     * @return A localized date string
     */
    public String asString(Date date) {
        // https://stackoverflow.com/questions/7796321/simpledateformat-pattern-based-on-locale-but-forcing-a-4-digit-year
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        StringBuffer buffer = new StringBuffer();
        StringBuffer format = dateFormat.format(date, buffer, yearPosition);
        format.replace(yearPosition.getBeginIndex(), yearPosition.getEndIndex(), String.valueOf(cal.get(Calendar.YEAR)));
        return format.toString();
    }

    /**
     * Convert date to string
     * @param date The date
     * @return A localized date string
     */
    public String asString(LocalDate date) {
        return asString(java.sql.Date.valueOf(date));

        /*return dateFormat.format(
                java.util.Date.from(date
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
        );*/
        //return dateFormat.format(date);
    }

    /**
     * Convert string to date
     * @param string The date string
     * @return The date
     * @throws ParseException
     */
    public LocalDate asLocalDate(String string) throws ParseException {
        // https://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate
        return dateFormat.parse(string).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Convert string to date
     * @param string The date string
     * @return The date
     * @throws ParseException
     */
    public Date asDate(String string) throws ParseException {
        return dateFormat.parse(string);
    }
}
