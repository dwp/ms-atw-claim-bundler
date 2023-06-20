package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleansingUtils {

  private static final Pattern ALPHA_PATTERN = Pattern.compile("\\p{Alpha}");

  private CleansingUtils() {
  }

  public static String removeNonLettersFromStart(String result) {
    Matcher m = ALPHA_PATTERN.matcher(result);
    if (m.find()) {
      result = result.substring(m.start());
    }
    return result;
  }

  public static String removeLastNumber(String value) {
    value = removeTrailingAllowedNonNumeric(value);
    if (isLastCharacterNumber(value)) {
      return removeLastNumber(value.substring(0, value.length() - 1));
    }
    return value;
  }

  private static String removeTrailingAllowedNonNumeric(String value) {
    return value.replaceAll("[.'-]$", "");
  }

  private static boolean isLastCharacterNumber(String value) {
    if (null == value || value.length() == 0) {
      return false;
    }

    return Character.isDigit(value.charAt(value.length() - 1));
  }
}
