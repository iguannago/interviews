package com.company.exercise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

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
        AtomicInteger value = new AtomicInteger(0);
        IntStream.range(0, romanNumerals.length()).forEach(index -> {
            char currentChar = romanNumerals.charAt(index);
            value.updateAndGet(v -> v + romans.get(String.valueOf(currentChar)));

            letterCanNotBeRepeatedThreeTimesCheck(romanNumerals);
        });

        return value.get();
    }


    private void letterCanNotBeRepeatedThreeTimesCheck(String romanNumerals) {
        if (romanNumerals.length() > 3) {
            AtomicInteger count = new AtomicInteger(0);
            AtomicReference<Character> currentChar = new AtomicReference<>();
            AtomicReference<Character> previousChar = new AtomicReference<>('0');

            IntStream.range(0, romanNumerals.length()).forEach(index -> {
                currentChar.set(romanNumerals.charAt(index));
                if (index > 0) {
                    previousChar.set(romanNumerals.charAt(index - 1));
                }
                if (currentChar.get().equals(previousChar.get())) {
                    count.getAndIncrement();
                }
            });

            if (count.get() == 3) {
                throw new IllegalArgumentException();
            }
        }
    }

}
