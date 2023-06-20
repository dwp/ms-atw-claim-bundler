package uk.gov.dwp.health.atw.msclaimbundler.connectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.awaitingUploadContactInformationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;

@SpringBootTest(classes = ContactInformationToPdfConnector.class)
class ContactInformationToPdfConnectorTest {

  @Autowired
  ContactInformationToPdfConnector connector;

  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(connector, "serviceUri", "http://localhost:9014");
  }

  @Test
  @DisplayName("generateAndUploadUpdateContactInformationPdf")
  public void generateAndUploadUpdateContactInformationPdf() {
    when(restTemplate.postForObject(eq("http://localhost:9014/generate/update-contact-details"), any(), any()))
        .thenReturn(new ClaimToPdfResponse("fileId"));

    ClaimToPdfResponse response = connector.generateAndUploadUpdateContactInformationPdf(
        awaitingUploadContactInformationRequest);

    assertEquals("fileId", response.getFileId());
  }

}