package uk.gov.dwp.health.atw.msclaimbundler.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.eaForm;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.swForm;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.ttwForm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ClaimFormConfigurationTests.class)
class ClaimFormConfigurationTests {

  @Test
  @DisplayName("Test EA Form Configuration")
  void testEaFormConfiguration() {
    assertEquals("DP224JP", eaForm.getAtwFormId());
    assertEquals(12067, eaForm.getDrsDocumentId());
  }

  @Test
  @DisplayName("Test SW Form Configuration")
  void testSwFormConfiguration() {
    assertEquals("DP222JP", swForm.getAtwFormId());
    assertEquals(12065, swForm.getDrsDocumentId());
  }

  @Test
  @DisplayName("Test TTW Form Configuration")
  void testTtwFormConfiguration() {
    assertEquals("DP226JP", ttwForm.getAtwFormId());
    assertEquals(12068, ttwForm.getDrsDocumentId());
  }

  @Test
  @DisplayName("get expection when calling utility function")
  void callingUtilityFunction() {
    assertThrows(IllegalStateException.class, () -> new ClaimFormConfiguration()
    );
  }
}
