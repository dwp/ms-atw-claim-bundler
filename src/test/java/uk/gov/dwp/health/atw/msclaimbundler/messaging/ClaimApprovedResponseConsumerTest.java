package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.UUID_MOCK;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;

@SpringBootTest(classes = {ClaimApprovedResponseConsumer.class})
class ClaimApprovedResponseConsumerTest {

  @Autowired
  private ClaimApprovedResponseConsumer claimApprovedResponseConsumer;

  @MockBean
  ClaimConnector claimConnector;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(claimApprovedResponseConsumer, "queue", "queue");
    ReflectionTestUtils.setField(claimApprovedResponseConsumer, "routingKey", "routingKey");
  }

  @Test
  void getQueueName() {
    assertEquals("queue", claimApprovedResponseConsumer.getQueueName());
  }

  @Test
  void getRoutingKey() {
    assertEquals("routingKey", claimApprovedResponseConsumer.getRoutingKey());
  }

  @Test
  @DisplayName("Success - true")
  void handleMessageSuccessTrue() {
    BatchUploadResponse response = new BatchUploadResponse();
    response.setSuccess(true);
    response.setRequestId(UUID_MOCK);

    doNothing().when(claimConnector).updateClaimStatusToAwaitingAgentApproval(UUID_MOCK);

    claimApprovedResponseConsumer.handleMessage(new MessageHeaders(Map.of()), response);

    verify(claimConnector, times(1)).updateClaimStatusToAwaitingAgentApproval(UUID_MOCK);
    verify(claimConnector, never()).updateClaimStatusToDrsError(UUID_MOCK);

  }


  @Test
  @DisplayName("Success - false")
  void handleMessageSuccessFalse() {
    BatchUploadResponse response = new BatchUploadResponse();
    response.setSuccess(false);
    response.setErrorMessage("Error occurred");
    response.setRequestId(UUID_MOCK);

    doNothing().when(claimConnector).updateClaimStatusToDrsError(UUID_MOCK);

    claimApprovedResponseConsumer.handleMessage(new MessageHeaders(Map.of()), response);

    verify(claimConnector, never()).updateClaimStatusToAwaitingAgentApproval(UUID_MOCK);
    verify(claimConnector, times(1)).updateClaimStatusToDrsError(UUID_MOCK);
  }
}