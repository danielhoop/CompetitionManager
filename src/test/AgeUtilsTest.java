package test;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.utils.AgeUtils;
import ch.ffhs.pa.competitionmanager.utils.DateStringConverter;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class AgeUtilsTest {

    // Attention! Is configured to work with this date format: dd.MM.YYYY
    @Test
    void isDateOfBirthPlausible() throws ParseException {
        DateStringConverter converter = new DateStringConverter(GlobalState.getInstance().getLocale());
        // In future
        assertEquals(AgeUtils.isDateOfBirthPlausible(converter.asLocalDate("01.01.2030")), false);
        // Too old (more than 100 years old)
        assertEquals(AgeUtils.isDateOfBirthPlausible(converter.asLocalDate("01.01.1900")), false);
        // OK.
        assertEquals(AgeUtils.isDateOfBirthPlausible(converter.asLocalDate("01.01.1999")), true);
    }

    @Test
    void calcAge() throws ParseException {
        DateStringConverter converter = new DateStringConverter(GlobalState.getInstance().getLocale());
        assertEquals(AgeUtils.calcAge(converter.asLocalDate("01.02.2000"), converter.asLocalDate("01.01.2001")), 0);
        assertEquals(AgeUtils.calcAge(converter.asLocalDate("01.02.2000"), converter.asLocalDate("31.01.2001")), 0);
        assertEquals(AgeUtils.calcAge(converter.asLocalDate("01.02.2000"), converter.asLocalDate("01.02.2001")), 1);
        assertEquals(AgeUtils.calcAge(converter.asLocalDate("01.02.2000"), converter.asLocalDate("31.01.2002")), 1);
        assertEquals(AgeUtils.calcAge(converter.asLocalDate("01.02.2000"), converter.asLocalDate("01.02.2002")), 2);
    }
}