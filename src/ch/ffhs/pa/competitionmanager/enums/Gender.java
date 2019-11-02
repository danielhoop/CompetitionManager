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

    public static Gender valueOf(int genderEncoded) {
        return (Gender) map.get(genderEncoded);
    }

    public int getValue() {
        return value;
    }
}
