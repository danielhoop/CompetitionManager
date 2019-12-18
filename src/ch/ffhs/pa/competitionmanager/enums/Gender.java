package ch.ffhs.pa.competitionmanager.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Gender enumeration. Includes functionality to convert between numeric encoding and enumeration.
 * Source: https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum
 */
public enum Gender {
    MALE(1), FEMALE(2), NOT_RELEVANT(3);

    private int value;
    private static Map map = new HashMap<>();

    private Gender(int value) {
        this.value = value;
    }

    static {
        for (Gender gender : Gender.values()) {
            map.put(gender.value, gender);
        }
    }

    /**
     * Convert integer encoding to Gender enumeration.
     * @param genderEncoded The encoded value
     * @return The value as Gender
     */
    public static Gender valueOf(int genderEncoded) {
        return (Gender) map.get(genderEncoded);
    }

    /**
     * Return the integer encoded value
     * @return The integer encoded value
     */
    public int getValue() {
        return value;
    }
}
