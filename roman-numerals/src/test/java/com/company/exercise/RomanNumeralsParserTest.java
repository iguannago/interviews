package com.company.exercise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.assertj.core.api.Assertions;
import org.junit.Test;

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
}
