package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;


/**
 * Utility class for validating and formatting UK Postcodes.
 * https://github.com/osgdev/Postcode
 */
public class PostcodeFormatter {

  private static String inCode = "";
  private static String outCode = "";
  private static final String[] IGNORELIST = new String[] {"AA22", "AA88", "AA89", "AA90", "AA91",
      "AA99", "WALES", "BT00"};
  //'official' regex, once used by Roayl Mail
  private static final String REGEX = " (GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]]"
      + "[A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9]"
      + "[ABEHMNPRVWXY]))))\\s?[0-9][A-Z-[CIKMOV]]{2})";

  /**
   * Formats a postcode by removing non-required zeros (e.g. S01 1JJ => S1 1JJ),
   * ensuring that outcode and incode parts
   * are separated by a space (e.g. S11JJ => S1 1JJ) and converting the string
   * to uppercase (e.g. s1 1jj => S1 1JJ).
   * Note: this does not validate the postcode. If validation is required,
   * use Postcode.validate() before using this method.
   *
   * @param postcode The unformatted postcode.
   * @return Postode formatted with spaces.
   */
  public static String format(String postcode) {

    String str = removeNonAcceptedCharacter(postcode);
    str = CleansingUtils.removeNonLettersFromStart(str);
    str = CleansingUtils.removeLastNumber(str);

    str = StringUtils.deleteWhitespace(str).toUpperCase();
    // Remove all * from the string
    str = str.replace("*", "");

    // DELETE POSTCODE IF MATCHING THE IGNORE LIST
    if (StringUtils.equalsAnyIgnoreCase(str, IGNORELIST)) {
      return "";
    }

    // IGNORE IF POSTCODE IS SHORTER THAN 5 CHARACTERS
    if (str.length() <= 4) {
      return str;
    }

    // Split postcode into outward and inward parts
    int outEnd = str.length() - 3;
    outCode = str.substring(0, outEnd);
    inCode = str.substring(outEnd);

    // Remove zeros: A01 1AA -> A1 1AA
    if (outCode.length() == 3 && StringUtils.isNumeric(outCode.substring(1))
        && StringUtils.mid(outCode, 1, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 1)
          + StringUtils.mid(outCode, 2, 1);
    }

    // Remove zeros: AA01 1AA -> AA1 1AA
    if (outCode.length() == 4 && StringUtils.isNumeric(outCode.substring(2))
        && StringUtils.mid(outCode, 2, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 2)
          + StringUtils.mid(outCode, 3, 1);
    }

    // Remove zeros: A01A 1AA -> A1A 1AA
    if (outCode.length() == 4 && !StringUtils.isNumeric(StringUtils.mid(outCode, 1, 3))
        && StringUtils.mid(outCode, 1, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 1)
          + StringUtils.mid(outCode, 2, 2);
    }

    // Remove zeros: AA01A 1AA -> AA1A 1AA
    if (outCode.length() == 5 && !StringUtils.isNumeric(StringUtils.mid(outCode, 4, 1))
        && StringUtils.mid(outCode, 2, 1).equals("0")) {
      outCode = StringUtils.mid(outCode, 0, 2)
          + StringUtils.mid(outCode, 3, 2);
    }

    // Join parts with a space
    return outCode + " " + inCode;
  }

  private static String removeNonAcceptedCharacter(String value) {
    String pattern = "[^a-zA-Z0-9]";
    return value.replaceAll(pattern, "");
  }

  /**
   * Validates the supplied string to check if it conforms to Royal Mail rules for a
   * valid postcode format.
   * Postcodes can be formatted with a space or without.
   *
   * @param postcode The postcode to validate.
   * @return True if a valid UK postcode.
   */
  public static boolean validate(String postcode) {
    Pattern pattern = Pattern.compile(REGEX);
    String temp = postcode.replace("*", "");
    temp = StringUtils.deleteWhitespace(temp).toUpperCase();
    return StringUtils.equalsAnyIgnoreCase(temp, IGNORELIST) || pattern.matcher(temp).matches();
  }
}