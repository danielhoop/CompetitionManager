package ch.ffhs.pa.competitionmanager.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateStringHandler {

    private DateFormat dateFormat;

    public DateStringHandler(Locale locale) {
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        // Alternative...
        // https://dotnetcodr.com/2015/01/17/localising-dates-in-java-8-using-datetimeformatter/
        // https://www.baeldung.com/java-datetimeformatter
        // DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
    }

    public String asString(LocalDate date) {
        return dateFormat.format(
                java.util.Date.from(date
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
        );
        //return dateFormat.format(date);
    }

    public LocalDate asLocalDate(String string) throws ParseException {
        // https://stackoverflow.com/questions/21242110/convert-java-util-date-to-java-time-localdate
        return dateFormat.parse(string).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Date asDate(String string) throws ParseException {
        return dateFormat.parse(string);
    }
}
