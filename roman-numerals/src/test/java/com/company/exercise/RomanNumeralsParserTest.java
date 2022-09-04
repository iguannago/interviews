package com.company.exercise;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RomanNumeralsParserTest {
    private final RomanNumeralsParser romanNumeralsParser = new RomanNumeralsParser();

    @Test
    public void shouldReturn1ForI() {
        int result = romanNumeralsParser.parse("I");

        assertEquals(1, result);
    }

    @Test
    public void shouldReturn5ForV() {
        assertEquals(5, romanNumeralsParser.parse("V"));
    }

    @Test
    public void shouldReturn10ForX() {
        assertEquals(10, romanNumeralsParser.parse("X"));
    }

    @Test
    public void shouldReturn50ForL() {
        assertEquals(50, romanNumeralsParser.parse("L"));
    }

    @Test
    public void shouldReturn100ForC() {
        assertEquals(100, romanNumeralsParser.parse("C"));
    }

    @Test
    public void shouldReturn1000ForM() {
        assertEquals(1000, romanNumeralsParser.parse("M"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldReturnErrorForXXXX() {
        romanNumeralsParser.parse("XXXX");
    }

    @Test
    public void shouldReturn30ForXXX() {
        romanNumeralsParser.parse("XXX");
    }

    @Test
    public void shouldReturn6ForVI() {
        assertEquals(6, romanNumeralsParser.parse("VI"));
    }
    @Test
    public void shouldReturn7ForVII() {
        assertEquals(7, romanNumeralsParser.parse("VII"));
    }
    @Test
    public void shouldReturn70ForLXX() {
        assertEquals(70, romanNumeralsParser.parse("LXX"));
    }
    @Test
    public void shouldReturn1200ForMCC() {
        assertEquals(1200, romanNumeralsParser.parse("MCC"));
    }
}
