package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.CONTACT_INFORMATION_ID;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;

@SpringBootTest(classes = {ContactInformationApprovedResponseConsumer.class})
class ContactInformationApprovedResponseConsumerTest {

  @Autowired
  private ContactInformationApprovedResponseConsumer contactInformationApprovedResponseConsumer;

  @MockBean
  ContactInformationConnector contactInformationConnector;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(contactInformationApprovedResponseConsumer, "queue", "queue");
    ReflectionTestUtils.setField(contactInformationApprovedResponseConsumer, "routingKey",
        "routingKey");
  }

  @Test
  void getQueueName() {
    assertEquals("queue", contactInformationApprovedResponseConsumer.getQueueName());
  }

  @Test
  @DisplayName("Success - true")
  void handleMessageSuccessTrue() {
    BatchUploadResponse success = new BatchUploadResponse();
    success.setSuccess(true);
    success.setRequestId(CONTACT_INFORMATION_ID);

    doNothing().when(contactInformationConnector).updateStatusToCompleted(CONTACT_INFORMATION_ID);

    contactInformationApprovedResponseConsumer.handleMessage(new MessageHeaders(Map.of()), success);

    verify(contactInformationConnector, times(1)).updateStatusToCompleted(CONTACT_INFORMATION_ID);
    verify(contactInformationConnector, never()).updateStatusToDrsError(CONTACT_INFORMATION_ID);

  }


  @Test
  @DisplayName("Success - false")
  void handleMessageSuccessFalse() {
    BatchUploadResponse success = new BatchUploadResponse();
    success.setSuccess(false);
    success.setErrorMessage("Error occurred");
    success.setRequestId(CONTACT_INFORMATION_ID);

    doNothing().when(contactInformationConnector).updateStatusToDrsError(CONTACT_INFORMATION_ID);

    contactInformationApprovedResponseConsumer.handleMessage(new MessageHeaders(Map.of()), success);

    verify(contactInformationConnector, never()).updateStatusToCompleted(CONTACT_INFORMATION_ID);
    verify(contactInformationConnector, times(1)).updateStatusToDrsError(CONTACT_INFORMATION_ID);
  }
}