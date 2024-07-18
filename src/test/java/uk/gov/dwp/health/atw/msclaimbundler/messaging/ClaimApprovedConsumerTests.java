package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.ADAPTATION_TO_VEHICLE;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.EQUIPMENT_OR_ADAPTATION;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.SUPPORT_WORKER;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.TRAVEL_IN_WORK;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.TRAVEL_TO_WORK;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.ATW_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.DECLARATION_VERSION;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.UUID_MOCK;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.claimant;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.evidences;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.expectedBatchUpload;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.expectedBatchUploadWithEnvelopeDocument;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.listOfEnvelopeDocument;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newPayee;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.payee;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
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
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.MinimalClaimBody;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ClaimReferenceNinoRequest;
import uk.gov.dwp.health.atw.msclaimbundler.services.ClaimService;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;

@SpringBootTest(classes = ClaimApprovedConsumer.class)
class ClaimApprovedConsumerTests {

  @Autowired
  ClaimApprovedConsumer claimApprovedConsumer;

  @MockBean
  ClaimService claimService;

  @MockBean
  ClaimConnector claimConnector;

  @MockBean
  FormGeneratorService formGeneratorService;

  @MockBean
  SnsPublisher snsPublisher;

  @MockBean
  ConsumerUtils consumerUtils;

  UUID defaultUuid = UUID.fromString(UUID_MOCK);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(claimApprovedConsumer, "queue", "queue");
    ReflectionTestUtils.setField(claimApprovedConsumer, "routingKey", "routingKey");
    ReflectionTestUtils.setField(claimApprovedConsumer, "drsQueueName", "drsQueueName");
    ReflectionTestUtils.setField(claimApprovedConsumer, "callerId", "callerId");
    ReflectionTestUtils.setField(claimApprovedConsumer, "responseRoutingKey", "responseRoutingKey");
  }

  @Test
  @DisplayName("check queue and topic are set")
  void checkQueueAndTopicAreSet() {
    assertEquals("queue", claimApprovedConsumer.getQueueName());
  }

  @Test
  @DisplayName("Process EA claim but ignore as wrong claimStatus")
  void processEaClaimWrongStatus() {
    String claimReference = "EA1234567";

    LocalDateTime now = LocalDateTime.now();
    MinimalClaimBody data = new MinimalClaimBody(
        NINO,
        ATW_NUMBER,
        EQUIPMENT_OR_ADAPTATION,
        claimant,
        now,
        ClaimStatus.UPLOADED_TO_DOCUMENT_BATCH,
        evidences,
        newPayee,
        DECLARATION_VERSION
    );

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
        objectMapper.convertValue(data, Map.class));

    claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
        ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
            .build());

    verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
    verify(claimService, never()).getEvidenceEnvelopDocumentsFromClaimData(any(), any());
    verify(claimConnector, never()).updateClaimStatusToUploadedToDocumentBatch(anyString(),
        anyString());
    verify(formGeneratorService, never()).generateAndUploadClaimPdf(any());
    verify(snsPublisher, never()).publishToDrs(any());

  }

  @Test
  @DisplayName("Process EA claim")
  void processEaClaim() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "EA1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/EA1234567";
      String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          EQUIPMENT_OR_ADAPTATION,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          newPayee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      when(formGeneratorService.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
          .thenReturn(new URL(newPayeeForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, times(1)).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);

      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.EQUIPMENT_OR_ADAPTATIONS);
      documentDetails.put(newPayeeForUrl, DocumentType.NEW_OR_AMENDED_DETAILS);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process AV claim but ignore as wrong claimStatus")
  void processAvClaimWrongStatus() {
    String claimReference = "AV1234567";

    LocalDateTime now = LocalDateTime.now();
    MinimalClaimBody data = new MinimalClaimBody(
        NINO,
        ATW_NUMBER,
        ADAPTATION_TO_VEHICLE,
        claimant,
        now,
        ClaimStatus.UPLOADED_TO_DOCUMENT_BATCH,
        evidences,
        newPayee,
        DECLARATION_VERSION
    );

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
        objectMapper.convertValue(data, Map.class));

    claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
        ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
            .build());

    verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
    verify(claimService, never()).getEvidenceEnvelopDocumentsFromClaimData(any(), any());
    verify(claimConnector, never()).updateClaimStatusToUploadedToDocumentBatch(anyString(),
        anyString());
    verify(formGeneratorService, never()).generateAndUploadClaimPdf(any());
    verify(snsPublisher, never()).publishToDrs(any());
  }

  @Test
  @DisplayName("Process AV claim")
  void processAvClaim() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "AV1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/AV1234567";
      String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          ADAPTATION_TO_VEHICLE,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          newPayee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      when(formGeneratorService.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
          .thenReturn(new URL(newPayeeForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, times(1)).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);

      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.ADAPTATION_TO_VEHICLE);
      documentDetails.put(newPayeeForUrl, DocumentType.NEW_OR_AMENDED_DETAILS);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process TW claim")
  void processTwClaim() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "TW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/TW1234567";
      String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          TRAVEL_TO_WORK,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          newPayee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      when(formGeneratorService.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
          .thenReturn(new URL(newPayeeForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, times(1)).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);


      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.TRAVEL_TO_WORK);
      documentDetails.put(newPayeeForUrl, DocumentType.NEW_OR_AMENDED_DETAILS);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process TW claim without new payee")
  void processTwClaimWithoutNewPayee() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "TW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/TW1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          TRAVEL_TO_WORK,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          payee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, never()).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);


      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.TRAVEL_TO_WORK);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process TIW claim")
  void processTiwClaim() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "TIW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/TIW1234567";
      String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          TRAVEL_IN_WORK,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          newPayee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      when(formGeneratorService.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
          .thenReturn(new URL(newPayeeForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, times(1)).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);


      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.TRAVEL_IN_WORK);
      documentDetails.put(newPayeeForUrl, DocumentType.NEW_OR_AMENDED_DETAILS);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process TIW claim without new payee")
  void processTiwClaimWithoutNewPayee() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "TIW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/TIW1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          TRAVEL_IN_WORK,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          payee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, never()).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);


      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.TRAVEL_IN_WORK);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process SW claim")
  void processSwClaim() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "SW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/SW1234567";
      String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          SUPPORT_WORKER,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          newPayee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      when(formGeneratorService.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
          .thenReturn(new URL(newPayeeForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, times(1)).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);


      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.SUPPORT_WORKER);
      documentDetails.put(newPayeeForUrl, DocumentType.NEW_OR_AMENDED_DETAILS);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process SW claim without new payee")
  void processSwClaimWithoutNewPayee() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "SW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/SW1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          SUPPORT_WORKER,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          evidences,
          payee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO)).thenReturn(
          mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim)).thenReturn(
          new URL(claimForUrl));

      when(claimService.getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate())).thenReturn(listOfEnvelopeDocument(now));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, times(1)).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, never()).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);


      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);

      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.SUPPORT_WORKER);

      assertThat(actualBatchUpload, samePropertyValuesAs(expectedBatchUploadWithEnvelopeDocument(
          now, documentDetails)));
    }
  }

  @Test
  @DisplayName("Process SW claim without new payee and evidence")
  void processSwClaimWithoutNewPayeeAndEvidence() throws MalformedURLException {
    try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class)) {
      mockedUuid.when(UUID::randomUUID).thenReturn(defaultUuid);

      String claimReference = "SW1234567";

      String claimForUrl = "http://localhost:8080/claims/claims/SW1234567";
      LocalDateTime now = LocalDateTime.now();
      MinimalClaimBody data = new MinimalClaimBody(
          NINO,
          ATW_NUMBER,
          SUPPORT_WORKER,
          claimant,
          now,
          ClaimStatus.AWAITING_DRS_UPLOAD,
          null,
          payee,
          DECLARATION_VERSION
      );

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.findAndRegisterModules();
      Map<String, Object> mapOfClaim = objectMapper.convertValue(data, Map.class);

      when(claimService.getClaimForClaimReferenceAndNino(claimReference, NINO))
          .thenReturn(mapOfClaim);

      when(formGeneratorService.generateAndUploadClaimPdf(mapOfClaim))
          .thenReturn(new URL(claimForUrl));

      doNothing().when(claimConnector)
          .updateClaimStatusToUploadedToDocumentBatch(claimReference, UUID_MOCK);
      doNothing().when(snsPublisher).publishToDrs(any());

      claimApprovedConsumer.handleMessage(new MessageHeaders(Map.of()),
          ClaimReferenceNinoRequest.builder().nino(NINO).claimReference(claimReference)
              .build());

      verify(claimService, times(1)).getClaimForClaimReferenceAndNino(claimReference, NINO);
      verify(claimService, never()).getEvidenceEnvelopDocumentsFromClaimData(data.getEvidence(),
          data.getCreatedDate());
      verify(formGeneratorService, times(1)).generateAndUploadClaimPdf(mapOfClaim);
      verify(formGeneratorService, never()).generateAndUploadNewPayeePdf(
          any(NewPayeeDetails.class));
      verify(claimConnector, times(1)).updateClaimStatusToUploadedToDocumentBatch(claimReference,
          UUID_MOCK);


      ArgumentCaptor<DocumentBatchEvent> argument =
          ArgumentCaptor.forClass(DocumentBatchEvent.class);

      verify(snsPublisher, times(1)).publishToDrs(argument.capture());

      BatchUpload actualBatchUpload =
          objectMapper.convertValue(argument.getValue().getPayload(), BatchUpload.class);

      LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
      documentDetails.put(claimForUrl, DocumentType.SUPPORT_WORKER);

      assertThat(actualBatchUpload,
          samePropertyValuesAs(expectedBatchUpload(now, documentDetails)));
    }
  }
}
