package uk.gov.dwp.health.atw.msclaimbundler.connectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.UUID_MOCK;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;

@SpringBootTest(classes = ClaimConnector.class)
class ClaimConnectorTests {

  @Autowired
  ClaimConnector connector;

  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(connector, "serviceUri", "http://localhost:9014");
  }

  @Test
  @DisplayName("getClaimForClaimReference")
  void getClaimForClaimReference() {

    when(restTemplate.postForObject(eq("http://localhost:9014/claim-for-reference-and-nino"), eq(Map.of("claimReference", "EA12345", "nino", NINO)),
        any())).thenReturn(Map.of("claimId", "claim"));

    Map<String, Object> response = connector.getClaimForClaimReference("EA12345", NINO);

    assertEquals(Map.of("claimId", "claim"), response);
  }

  @Test
  @DisplayName("updateClaimStatusToUploadedToDocumentBatch")
  void updateClaimStatusToUploadedToDocumentBatch() {

    doNothing().when(restTemplate).put("http://localhost:9014/change-status/uploaded-to-document-batch", Map.of("claimReference", "EA12345", "requestId", UUID_MOCK));

    connector.updateClaimStatusToUploadedToDocumentBatch("EA12345", UUID_MOCK);

    verify(restTemplate).put("http://localhost:9014/change-status/uploaded-to-document-batch", Map.of("claimReference", "EA12345", "requestId", UUID_MOCK));
  }


  @Test
  @DisplayName("updateClaimStatusToDrsError")
  void updateClaimStatusToDrsError() {

    doNothing().when(restTemplate).put("http://localhost:9014/change-status/drs-error", Map.of("requestId", UUID_MOCK));

    connector.updateClaimStatusToDrsError( UUID_MOCK);

    verify(restTemplate).put("http://localhost:9014/change-status/drs-error", Map.of("requestId", UUID_MOCK));
  }

  @Test
  @DisplayName("updateClaimStatusToAwaitingAgentApproval")
  void updateClaimStatusToAwaitingAgentApproval() {

    doNothing().when(restTemplate).put("http://localhost:9014/change-status/awaiting-agent-approval", Map.of("requestId", UUID_MOCK));

    connector.updateClaimStatusToAwaitingAgentApproval( UUID_MOCK);

    verify(restTemplate).put("http://localhost:9014/change-status/awaiting-agent-approval", Map.of("requestId", UUID_MOCK));
  }
}
