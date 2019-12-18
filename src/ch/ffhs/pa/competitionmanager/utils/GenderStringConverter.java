package ch.ffhs.pa.competitionmanager.utils;

import ch.ffhs.pa.competitionmanager.core.GlobalState;
import ch.ffhs.pa.competitionmanager.enums.Gender;
import ch.ffhs.pa.competitionmanager.enums.SupportedLocale;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class to convert between Gender enumeration and string representation.
 */
public class GenderStringConverter {

    private GlobalState globalState;
    private ResourceBundle bundle;
    private SupportedLocale localeName;

    /**
     * Constructor, internally depending on GlobalState and the GuiText resource bundle.
     */
    public GenderStringConverter() {
        globalState = GlobalState.getInstance();
        bundle = globalState.getGuiTextBundle();
        localeName = globalState.getLocalName();
    }

    /**
     * Convert Gender enumeration to string
     * @param gender The gender
     * @return The string representing the gender
     */
    public String asString(Gender gender) {
        if (gender == Gender.MALE)
            return bundle.getString("Gender.MALE");
        if (gender == Gender.FEMALE)
            return bundle.getString("Gender.FEMALE");
        if (gender == Gender.NOT_RELEVANT)
            return bundle.getString("Gender.NOT_RELEVANT");
        return gender.toString();
    }
}
