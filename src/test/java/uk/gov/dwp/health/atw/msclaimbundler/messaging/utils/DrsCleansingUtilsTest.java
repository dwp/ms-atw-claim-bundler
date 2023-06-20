package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DrsCleansingUtils.class)
class DrsCleansingUtilsTest {

  @Test
  @DisplayName("valid value with no extra characters for drs")
  void validateAndCleansingValueForDrsSuccessful() {
    assertEquals("John", DrsCleansingUtils.validateAndCleanseNameForDrs("John"));
  }

  @ParameterizedTest
  @CsvSource({
      "D'Arcy, D'Arcy",
      "Jo.hn, Jo.hn",
      "John-William, John-William",
      "Jo hn, Jo hn"
  })
  @DisplayName("valid value with non-alphanumeric characters for drs")
  void validateAndCleansingValueWithValidNonAlphaNumericCharactersForDrsSuccessful(
      String input,
      String expected) {
    assertEquals(expected, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
  }

  @ParameterizedTest
  @CsvSource({
      "2332johN5Pn, JohN5Pn",
      "1john w4lliam, John w4lliam"
  })
  @DisplayName("remove numbers from start of value")
  void validateAndCleanseNameRemoveNumbersFromStartOfValue(
      String input,
      String expected) {
    assertEquals(expected, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
  }

  @Test
  @DisplayName("remove invalid number from the end of a value")
  void validateAndCleansingValueWithNumberAtTheEnd() {
    assertEquals("John", DrsCleansingUtils.validateAndCleanseNameForDrs("John33"));
  }

  @Test
  @DisplayName("invalid value starting with a lowercase")
  void validateAndCleansingValueStartingWithLowercaseLetter() {
    assertEquals("John", DrsCleansingUtils.validateAndCleanseNameForDrs("john"));
  }

  @Test
  @DisplayName("remove apostrophe from start of name")
  void validateAndCleansingValueStartingWithApostrophe() {
    assertEquals("John", DrsCleansingUtils.validateAndCleanseNameForDrs("'John"));
  }

  @Test
  @DisplayName("remove white space from start of name")
  void validateAndCleansingValueStartingWithWhiteSpace() {
    assertEquals("John", DrsCleansingUtils.validateAndCleanseNameForDrs(" John"));
  }

  @ParameterizedTest
  @CsvSource({
      ".John, John",
      "-John, John"
  })
  @DisplayName("removing non-alphanumeric characters from the start of a value")
  void validateAndCleanseNameStartingWithValidNonAlphanumericCharacters(
      String input,
      String expected) {
    assertEquals(expected, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
  }

  @Test
  @DisplayName("cleansing invalid value with a full stop and white space at the end for drs")
  void validateAndCleansingValueEndingWithFullStopAndWhiteSpaceForDrs() {
    assertEquals("John William", DrsCleansingUtils.validateAndCleanseNameForDrs("John William. "));
  }

  @ParameterizedTest
  @CsvSource({
      "John William', John William",
      "john William23-1-23, John William",
      "John William-, John William",
      "John William., John William"
  })
  @DisplayName("removing valid non-alpha characters from the end of a value")
  void validateAndCleanseNameEndingWithValidNonAlphanumericCharacters(
      String input,
      String expected) {
    assertEquals(expected, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
  }

  @Test
  @DisplayName("cleansing invalid value spaces before or after punctuation")
  void validateAndCleansingValueWhereSpacesAreBeforeOrAfterPunctuationDrs() {
    assertEquals("John Will ia m",
        DrsCleansingUtils.validateAndCleanseNameForDrs("John Will. ia .m"));
  }

  @Test
  @DisplayName("cleansing invalid value with white space at the end for drs")
  void validateAndCleansingValueEndingWithWhiteSpaceForDrs() {
    assertEquals("John William", DrsCleansingUtils.validateAndCleanseNameForDrs("John William "));
  }

  @Test
  @DisplayName("cleansing invalid value with non-alphanumeric characters which are not allowed")
  void validateAndCleansingValueWithInvalidNonAlphanumericCharactersForDrs() {
    assertEquals("John Wllam Jne Mry",
        DrsCleansingUtils.validateAndCleanseNameForDrs("John W\"ll*am & J£ne M%ry"));
  }

  @ParameterizedTest
  @CsvSource({
      "Jo''hn william, Jo hn william",
      "te.'-st,  Te st",
      "te.....st, Te st",
      "John--William charles, John William charles",
      "D''Arcy jane, D Arcy jane",
      "jo- hn will  iam, Jo hn will iam"
  })
  @DisplayName("validate and cleanse consecutive non-alphanumeric Characters")
  void validateAndCleansingValueWithConsecutiveNonAlphaNumericCharactersForDrsSuccessful(
      String input,
      String expected) {
    assertEquals(expected, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
  }

  @Test
  @DisplayName("cleansing valid value with more than 70 characters for drs")
  void validateAndCleansingValueWithMoreThan70CharactersForDrsSuccessful() {
    int expectedLength = 70;
    String input = "nulla facilisi etiam dignissim diam quis enim lobortis scelerisque ferg1";
    String expectedOutput =
        "Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fer";

    assertEquals(expectedOutput, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
    assertEquals(expectedLength, DrsCleansingUtils.validateAndCleanseNameForDrs(input).length());
  }

  @Test
  @DisplayName("cleansing valid value with 69 characters for drs")
  void validateAndCleansingValueWith69CharactersForDrsSuccessful() {
    int expectedLength = 69;
    String input = "nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fe";
    String expectedOutput =
        "Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fe";

    assertEquals(expectedOutput, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
    assertEquals(expectedLength, DrsCleansingUtils.validateAndCleanseNameForDrs(input).length());
  }

  @Test
  @DisplayName("cleansing valid value with 70 characters for drs")
  void validateAndCleansingValueAt70CharactersForDrsSuccessful() {
    int expectedLength = 70;
    String input = "Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fer";
    String expectedOutput =
        "Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fer";

    assertEquals(expectedOutput, DrsCleansingUtils.validateAndCleanseNameForDrs(input));
    assertEquals(expectedLength, DrsCleansingUtils.validateAndCleanseNameForDrs(input).length());
  }

  // test postcode
  @Test
  @DisplayName("cleansing postcode with spaces at the beginning and end")
  void validateAndCleansingPostcodeWithSpaceAtBeginningAndEnd() {
    assertEquals("NE26 4RS",
        DrsCleansingUtils.validateAndCleansePostcodeForDrs(" ne264rs "));
  }

  @Test
  @DisplayName("cleansing postcode without spaces in middle")
  void validateAndCleansingPostcodeWithoutSpaceInMiddle() {
    assertEquals("A9 9DD",
        DrsCleansingUtils.validateAndCleansePostcodeForDrs("A99DD"));
  }

  @Test
  @DisplayName("cleansing postcode with a invalid characters")
  void validateAndCleansingPostcodeWithInvalidCharacters() {
    assertEquals("A9 9DD",
        DrsCleansingUtils.validateAndCleansePostcodeForDrs(" A9.9DD "));
  }

  @Test
  @DisplayName("cleansing postcode with multiple invalid characters")
  void validateAndCleansingPostcodeWithMultipleInvalidCharacters() {
    assertEquals("A9 9DD",
        DrsCleansingUtils.validateAndCleansePostcodeForDrs("87A'9.-9D  D87"));
  }

  @ParameterizedTest
  @CsvSource({
      "A01 1AA, A1 1AA",
      "AA01 1AA, AA1 1AA",
      "AA01A 1AA, AA1A 1AA",
      "A01A 1AA, A1A 1AA"
  })
  @DisplayName("validate and cleanse postcodes")
  void validateAndCleansingPostcodeWithManyVariation(String input, String expected) {
    assertEquals(expected,
        DrsCleansingUtils.validateAndCleansePostcodeForDrs(input));
  }

  @ParameterizedTest
  @CsvSource({
      "A01 1AA,true",
      "AA01 1AA, true",
      "AA01A 1AA, false",
      "A01A 1AA, false"
  })
  @DisplayName("validate series of postcodes are validated")
  void validatePostcodeWithManyVariation(String input, boolean expected) {
    assertEquals(expected, PostcodeFormatter.validate(input));
  }
}