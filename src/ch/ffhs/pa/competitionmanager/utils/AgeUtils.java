package ch.ffhs.pa.competitionmanager.utils;

import java.time.LocalDate;

public class AgeUtils {

    public static boolean isDateOfBirthPlausible(LocalDate dateOfBirth) {
        LocalDate now = LocalDate.now();
        if (dateOfBirth.getYear() > now.getYear() ||
                (dateOfBirth.getYear() == now.getYear() && dateOfBirth.getDayOfYear() > now.getDayOfYear()))
            return false;
        if (now.getYear() - dateOfBirth.getYear() > 100)
            return false;
        return true;
    }

    public static int calcAge(LocalDate dateOfBirth, LocalDate referenceDate) {
        int age = referenceDate.getYear() - dateOfBirth.getYear();
        if (dateOfBirth.getDayOfYear() < referenceDate.getDayOfYear())
            age--;
        return age;
    }

}
