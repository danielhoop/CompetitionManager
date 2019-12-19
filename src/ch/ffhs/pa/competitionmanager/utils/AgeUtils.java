package ch.ffhs.pa.competitionmanager.utils;

import java.time.LocalDate;

/**
 * Utility class to handle date of birth and age.
 */
public class AgeUtils {

    /**
     * Check if date of birth is plausible (i.e. not in future and competitor is not more than 100 years old)
     * @param dateOfBirth The date of birth
     * @return True indicates that the given date of birth is plausible.
     */
    public static boolean isDateOfBirthPlausible(LocalDate dateOfBirth) {
        LocalDate now = LocalDate.now();
        if (dateOfBirth.getYear() > now.getYear() ||
                (dateOfBirth.getYear() == now.getYear() && dateOfBirth.getDayOfYear() > now.getDayOfYear()))
            return false;
        if (now.getYear() - dateOfBirth.getYear() > 100)
            return false;
        return true;
    }

    /**
     * Calculate the age from a given date of birth.
     * @param dateOfBirth The date of brith
     * @param referenceDate The reference date (as of...)
     * @return The age
     */
    public static int calcAge(LocalDate dateOfBirth, LocalDate referenceDate) {
        int age = referenceDate.getYear() - dateOfBirth.getYear();
        if (referenceDate.getDayOfYear() < dateOfBirth.getDayOfYear())
            age--;
        return age;
    }

}
