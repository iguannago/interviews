package com.company.exercise;

import java.util.HashMap;
import java.util.Map;

public class RomanNumeralsParser {

    private static final Map<String, Integer> romans = new HashMap<>();

    static {
        romans.put("I", 1);
        romans.put("V", 5);
        romans.put("X", 10);
        romans.put("L", 50);
        romans.put("C", 100);
        romans.put("M", 1000);
    }

    int parse(String romanNumerals) {
        int count = 0;
        Integer value = 0;
        for (int i = 0; i < romanNumerals.length(); i++) {
            char currentChar = romanNumerals.charAt(i);
            value += romans.get(String.valueOf(currentChar));
            char previousChar = 0;

            if (i > 0) {
                previousChar = romanNumerals.charAt(i - 1);
            }
            if (currentChar == previousChar) {
                count++;
            }
            if (count == 3) {
                throw new IllegalArgumentException("more than three consecutive chars");
            }

        }

        return value;
    }
}
