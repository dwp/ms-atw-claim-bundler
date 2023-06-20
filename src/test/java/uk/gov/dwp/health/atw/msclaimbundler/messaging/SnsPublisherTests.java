package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.UUID_MOCK;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.expectedBatchUploadWithEnvelopeDocument;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchResponseEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.TestInboundClaimReferenceMessage;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;
import uk.gov.dwp.health.integration.message.events.EventManager;
import uk.gov.dwp.health.integration.message.events.QueueEventManager;

@SpringBootTest(classes = SnsPublisher.class)
class SnsPublisherTests {

  @Autowired
  private SnsPublisher snsPublisher;

  @MockBean
  EventManager eventManager;

  @MockBean
  QueueEventManager queueEventManager;

  @Test
  @DisplayName("Should publish a batch upload event")
  void publishToDrs() {
    String claimForUrl = "http://localhost:8080/claims/claims/EA1234567";

    LinkedHashMap<String, DocumentType> documentDetails = new LinkedHashMap<>();
    documentDetails.put(claimForUrl, DocumentType.EQUIPMENT_OR_ADAPTATIONS);

    BatchUpload upload =
        expectedBatchUploadWithEnvelopeDocument(LocalDateTime.now(), documentDetails);
    // given
    DocumentBatchEvent event = new DocumentBatchEvent("queue", upload);

    doNothing().when(queueEventManager).send(event);

    // when
    snsPublisher.publishToDrs(event);

    verify(queueEventManager, times(1)).send(event);
  }


  @Test
  @DisplayName("Should publish to test")
  void sendTestMessage() {

    Map<String, Object> upload = Map.of("atwNumber", "12345678");

    TestInboundClaimReferenceMessage event = new TestInboundClaimReferenceMessage(upload);
    doNothing().when(eventManager).send(event);

    // when
    snsPublisher.sendTestMessage(upload);

    ArgumentCaptor<TestInboundClaimReferenceMessage> argument =
        ArgumentCaptor.forClass(TestInboundClaimReferenceMessage.class);


    verify(eventManager, times(1)).send(argument.capture());
    assertThat(argument.getValue().getPayload(), samePropertyValuesAs(event.getPayload()));

  }


  @Test
  @DisplayName("Should publish test DRS response")
  void sendDrsResponse() {
    BatchUploadResponse response = new BatchUploadResponse();
    response.setRequestId(UUID_MOCK);
    response.setSuccess(true);

    doNothing().when(queueEventManager).send(any(DocumentBatchResponseEvent.class));

    DocumentBatchResponseEvent send = new DocumentBatchResponseEvent("drsqueue", response);

    snsPublisher.sendDrsResponse(send);

    ArgumentCaptor<DocumentBatchResponseEvent> argument =
        ArgumentCaptor.forClass(DocumentBatchResponseEvent.class);


    verify(queueEventManager, times(1)).send(argument.capture());
    assertThat(argument.getValue().getPayload(), samePropertyValuesAs(send.getPayload()));

  }
}
