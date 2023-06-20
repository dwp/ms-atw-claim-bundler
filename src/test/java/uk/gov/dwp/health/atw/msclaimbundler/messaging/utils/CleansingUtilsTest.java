package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CleansingUtils.class)
class CleansingUtilsTest {

  @ParameterizedTest
  @CsvSource({
      "1111John smith, John smith",
      ".John smith, John smith",
      "-John-William, John-William",
      "£John smi.th, John smi.th",
      "John smi..th, John smi..th",
      "John smi£th, John smi£th"
  })
  @DisplayName("remove non letters from start of value")
  void removeNonLettersFromStartOfValue(String input,
                                        String expected) {
    assertEquals(expected, CleansingUtils.removeNonLettersFromStart(input));
  }

  @ParameterizedTest
  @CsvSource({
      "John smith1111, John smith",
      "John smith2, John smith",
      "2John-William, 2John-William",
      "John smi3th, John smi3th",
      "John smi.th2.3, John smi.th",
      "John smi.th23-23-23, John smi.th",
      "John smith-, John smith",
      "John smith', John smith",
      "John smith., John smith",
      "John smith.., John smith.",
      "John smith---, John smith--"
  })
  @DisplayName("remove last number from value")
  void removeLastNumber(String input,
                        String expected) {
    assertEquals(expected, CleansingUtils.removeLastNumber(input));
  }

  @Test
  @DisplayName("remove last number with no value")
  void removeLastNumberWithNoValue() {
    assertEquals("", CleansingUtils.removeLastNumber(""));
  }
}