package uk.gov.dwp.health.atw.msclaimbundler.connectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newPayeeDetails;

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

@SpringBootTest(classes = ClaimToPdfConnector.class)
class ClaimToPdfConnectorTests {

  @Autowired
  ClaimToPdfConnector connector;

  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(connector, "serviceUri", "http://localhost:9015");
  }

  @Test
  @DisplayName("generateAndUploadClaimPdf Successful")
  void generateAndUploadClaimPdfSuccessful() {

    when(restTemplate.postForObject(eq("http://localhost:9015/generate/claim-form"), any(),
        any())).thenReturn(
        new ClaimToPdfResponse("fileId"));

    ClaimToPdfResponse response = connector.generateAndUploadClaimPdf(Map.of("claim", "data"));

    assertEquals("fileId", response.getFileId());
  }

  @Test
  @DisplayName("generateAndUploadNewPayeePdf Successful")
  void generateAndUploadNewPayeePdfSuccessful() {

    when(restTemplate.postForObject(eq("http://localhost:9015/generate/create-payee"), any(), any()))
        .thenReturn(new ClaimToPdfResponse("fileId"));

    ClaimToPdfResponse response = connector.generateAndUploadNewPayeePdf(newPayeeDetails);

    assertEquals("fileId", response.getFileId());
  }

}
