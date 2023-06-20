package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import org.springframework.util.StringUtils;

public class DrsCleansingUtils {


  public static String validateAndCleanseNameForDrs(String valueForCleansing) {

    String result = removeNonAcceptedCharacter(valueForCleansing.trim());

    result = CleansingUtils.removeNonLettersFromStart(result);

    result = CleansingUtils.removeLastNumber(result);

    result = trimTo70Characters(result);

    result = removeConsecutiveChar(result);

    return capitalizeFirstChar(result);
  }

  private static String trimTo70Characters(String result) {
    if (result.length() > 70) {
      result = result.substring(0, 70);
    }
    return result;
  }

  private static String capitalizeFirstChar(String value) {
    return StringUtils.capitalize(value);
  }

  private static String removeNonAcceptedCharacter(String value) {
    String pattern = "[^a-zA-Z0-9-' .]";
    return value.replaceAll(pattern, "");
  }

  private static String removeConsecutiveChar(String value) {
    return value.replaceAll("[.'-/ ]{2,}", " ");
  }

  public static String validateAndCleansePostcodeForDrs(String valueForCleansing) {
    return PostcodeFormatter.format(valueForCleansing);
  }

}
