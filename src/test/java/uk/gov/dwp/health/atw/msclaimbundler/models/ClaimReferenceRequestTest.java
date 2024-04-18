package uk.gov.dwp.health.atw.msclaimbundler.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.atw.msclaimbundler.models.exceptions.InvalidClaimReferenceFormat;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ClaimReferenceRequest;

class ClaimReferenceRequestTest {

  @Test
  @DisplayName("Test ClaimReferenceRequest for EA")
  void testClaimReferenceRequestForEA() throws InvalidClaimReferenceFormat {
    ClaimReferenceRequest request = new ClaimReferenceRequest("EA1");
    assertEquals("EA", request.getClaimType().label);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for AV")
  void testClaimReferenceRequestForAV() throws InvalidClaimReferenceFormat {
    ClaimReferenceRequest request = new ClaimReferenceRequest("AV1");
    assertEquals("AV", request.getClaimType().label);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for SW")
  void testClaimReferenceRequestForSW() throws InvalidClaimReferenceFormat {
    ClaimReferenceRequest request = new ClaimReferenceRequest("SW1");
    assertEquals("SW", request.getClaimType().label);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for TW")
  void testClaimReferenceRequestForTW() throws InvalidClaimReferenceFormat {
    ClaimReferenceRequest request = new ClaimReferenceRequest("TW1");
    assertEquals("TW", request.getClaimType().label);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for TIW")
  void testClaimReferenceRequestForTIW() throws InvalidClaimReferenceFormat {
    ClaimReferenceRequest request = new ClaimReferenceRequest("TIW1");
    assertEquals("TIW", request.getClaimType().label);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for TW1T0")
  void testClaimReferenceRequestForFrontLengthOfLetters() {
    ClaimReferenceRequest request = new ClaimReferenceRequest("TW1T0");
    assertThrows(InvalidClaimReferenceFormat.class, request::getClaimType);
  }

  @Test
  @DisplayName("Test ClaimReferenceRequest for TI")
  void testClaimReferenceRequestForInvalidClaimType() {
    ClaimReferenceRequest request = new ClaimReferenceRequest("TI1");
    assertThrows(InvalidClaimReferenceFormat.class, request::getClaimType);
  }
}
