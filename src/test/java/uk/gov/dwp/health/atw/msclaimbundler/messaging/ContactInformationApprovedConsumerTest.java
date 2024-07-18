package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.CONTACT_INFORMATION_ID;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.currentContactInformation;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.newContactInformation;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.ATW_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.CREATED_DATE;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.DECLARATION_VERSION;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.UUID_MOCK;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.expectedBatchUpload;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ContactInformationStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactRetrievalRequest;
import uk.gov.dwp.health.atw.msclaimbundler.services.ContactInformationService;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;


@SpringBootTest(classes = ContactInformationApprovedConsumer.class)
class ContactInformationApprovedConsumerTest {

  @Autowired
  private ContactInformationApprovedConsumer contactInformationApprovedConsumer;

  @MockBean
  ContactInformationService contactInformationService;

  @MockBean
  ContactInformationConnector contactInformationConnector;

  @MockBean
  FormGeneratorService formGeneratorService;

  @MockBean
  SnsPublisher snsPublisher;

  @MockBean
  ConsumerUtils consumerUtils;

  UUID defaultUuid = UUID.fromString(UUID_MOCK);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(contactInformationApprovedConsumer, "queue", "queue");
    ReflectionTestUtils.setField(contactInformationApprovedConsumer, "routingKey", "routingKey");
    ReflectionTestUtils.setField(contactInformationApprovedConsumer, "drsQueueName",
        "drsQueueName");
    ReflectionTestUtils.setField(contactInformationApprovedConsumer, "callerId", "callerId");
    ReflectionTestUtils.setField(contactInformationApprovedConsumer, "responseRoutingKey",
        "responseRoutingKey");
  }

  @Test
  @DisplayName("check queue and topic are set")
  void checkQueueAndTopicAreSet() {
    assertEquals("queue", contactInformationApprovedConsumer.getQueueName());
  }

  @Test
  @DisplayName(" processing the handle Message method")
  void handleMessageSuccess() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String contactInfoFormS3Url = "http://localhost:8080/generate/updateContact/";

      ContactInformationRequest awaitingUploadContactInformationRequest =
          ContactInformationRequest.builder()
              .id(CONTACT_INFORMATION_ID)
              .accessToWorkNumber(ATW_NUMBER)
              .nino(NINO)
              .declarationVersion(DECLARATION_VERSION)
              .currentContactInformation(currentContactInformation)
              .newContactInformation(newContactInformation)
              .createdDate(CREATED_DATE)
              .contactInformationStatus(ContactInformationStatus.AWAITING_UPLOAD)
              .build();

      when(contactInformationService.getContactInformationForId(CONTACT_INFORMATION_ID))
          .thenReturn(awaitingUploadContactInformationRequest);

      doNothing().when(contactInformationConnector)
          .updateContactInformationStatusToProcessingUpload(CONTACT_INFORMATION_ID);

      when(formGeneratorService.generateAndUploadUpdateContactInformationPdf(
          awaitingUploadContactInformationRequest))
          .thenReturn(new URL(contactInfoFormS3Url));


      doNothing().when(contactInformationConnector)
          .updateContactInformationStatusToUploadedToDocumentBatch(CONTACT_INFORMATION_ID);
      doNothing().when(snsPublisher).publishToDrs(any());

      contactInformationApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ContactRetrievalRequest.builder().id(CONTACT_INFORMATION_ID).build());

      verify(contactInformationService, times(1)).getContactInformationForId(
          CONTACT_INFORMATION_ID);
      verify(formGeneratorService, times(1)).generateAndUploadUpdateContactInformationPdf(
          awaitingUploadContactInformationRequest);
      verify(contactInformationConnector,
          times(1)).updateContactInformationStatusToProcessingUpload(CONTACT_INFORMATION_ID);
      verify(contactInformationConnector,
          times(1)).updateContactInformationStatusToUploadedToDocumentBatch(CONTACT_INFORMATION_ID);

      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);

      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);

      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(contactInfoFormS3Url, DocumentType.NEW_OR_AMENDED_DETAILS);

      BatchUpload expectedResponse = expectedBatchUpload(CREATED_DATE, documentDetails);

      expectedResponse.correlationId(UUID_MOCK).requestId(CONTACT_INFORMATION_ID);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedResponse));
    }
  }

  @Test
  @DisplayName("processing the handle Message method")
  void handleMessageFailure() {

    ContactInformationRequest processingUploadContactInformationRequest =
        ContactInformationRequest.builder()
            .id(CONTACT_INFORMATION_ID)
            .accessToWorkNumber(ATW_NUMBER)
            .nino(NINO)
            .declarationVersion(DECLARATION_VERSION)
            .currentContactInformation(currentContactInformation)
            .newContactInformation(newContactInformation)
            .createdDate(CREATED_DATE)
            .contactInformationStatus(ContactInformationStatus.PROCESSING_UPLOAD)
            .build();

    when(contactInformationService.getContactInformationForId(CONTACT_INFORMATION_ID))
        .thenReturn(processingUploadContactInformationRequest);

    contactInformationApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
        ContactRetrievalRequest.builder().id(CONTACT_INFORMATION_ID).build());

    verify(contactInformationService, times(1)).getContactInformationForId(CONTACT_INFORMATION_ID);
    verify(formGeneratorService, never()).generateAndUploadUpdateContactInformationPdf(
        processingUploadContactInformationRequest);
    verify(contactInformationConnector, never()).updateContactInformationStatusToProcessingUpload(
        CONTACT_INFORMATION_ID);
    verify(contactInformationConnector,
        never()).updateContactInformationStatusToUploadedToDocumentBatch(CONTACT_INFORMATION_ID);
  }
}