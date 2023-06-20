package uk.gov.dwp.health.atw.msclaimbundler.connectors;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.CONTACT_INFORMATION_ID;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.awaitingUploadContactInformationRequest;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;

@SpringBootTest(classes = ContactInformationConnector.class)
class ContactInformationConnectorTest {

  @Autowired
  ContactInformationConnector connector;

  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(connector, "serviceUri", "http://localhost:9014");
  }

  @Test
  @DisplayName("getContactInformationForIdAndAtwNumber")
  void getContactInformationForIdAndAtwNumber() {

    when(restTemplate.postForObject(eq("http://localhost:9014/retrieve-contact-information"), eq(
            Map.of("requestId", CONTACT_INFORMATION_ID)),
        any())).thenReturn(awaitingUploadContactInformationRequest);

    ContactInformationRequest response =
        connector.getContactInformationForId(CONTACT_INFORMATION_ID);

    assertEquals(awaitingUploadContactInformationRequest, response);
  }

  @Test
  @DisplayName("updateContactInformationStatusToProcessingUpload")
  void updateContactInformationStatusToProcessingUpload() {

    doNothing().when(restTemplate)
        .put("http://localhost:9014/contact/change-status/processing-upload",
            Map.of("requestId", CONTACT_INFORMATION_ID));

    connector.updateContactInformationStatusToProcessingUpload(CONTACT_INFORMATION_ID);

    verify(restTemplate).put("http://localhost:9014/contact/change-status/processing-upload",
        Map.of("requestId", CONTACT_INFORMATION_ID));
  }

  @Test
  @DisplayName("updateContactInformationStatusToUploadedToDocumentBatch")
  void updateContactInformationStatusToUploadedToDocumentBatch() {

    doNothing().when(restTemplate)
        .put("http://localhost:9014/contact/change-status/uploaded-to-document-batch",
            Map.of("requestId", CONTACT_INFORMATION_ID));

    connector.updateContactInformationStatusToUploadedToDocumentBatch(CONTACT_INFORMATION_ID);

    verify(restTemplate).put(
        "http://localhost:9014/contact/change-status/uploaded-to-document-batch",
        Map.of("requestId", CONTACT_INFORMATION_ID));
  }

  @Test
  @DisplayName("updateStatusToCompleted")
  void updateStatusToCompleted() {

    doNothing().when(restTemplate)
        .put("http://localhost:9014/contact/change-status/completed-upload",
            Map.of("requestId", CONTACT_INFORMATION_ID));

    connector.updateStatusToCompleted(CONTACT_INFORMATION_ID);

    verify(restTemplate).put("http://localhost:9014/contact/change-status/completed-upload",
        Map.of("requestId", CONTACT_INFORMATION_ID));
  }

  @Test
  @DisplayName("updateStatusToDrsError")
  void updateStatusToDrsError() {

    doNothing().when(restTemplate)
        .put("http://localhost:9014/contact/change-status/failed-drs-upload",
            Map.of("requestId", CONTACT_INFORMATION_ID));

    connector.updateStatusToDrsError(CONTACT_INFORMATION_ID);

    verify(restTemplate).put("http://localhost:9014/contact/change-status/failed-drs-upload",
        Map.of("requestId", CONTACT_INFORMATION_ID));
  }
}