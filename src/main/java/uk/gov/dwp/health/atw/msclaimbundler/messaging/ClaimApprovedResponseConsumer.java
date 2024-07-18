package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;
import uk.gov.dwp.health.integration.message.consumers.HealthMessageConsumer;

@Slf4j
@Service
public class ClaimApprovedResponseConsumer
    implements HealthMessageConsumer<BatchUploadResponse> {

  final ClaimConnector claimConnector;


  @Value("${service.consumer.new-claim-response.queue}")
  String queue;

  @Value("${document-batch.responseRoutingKey.claim}")
  String routingKey;

  public ClaimApprovedResponseConsumer(ClaimConnector claimConnector) {
    this.claimConnector = claimConnector;
  }

  @Override
  public String getQueueName() {
    return queue;
  }

  @SneakyThrows
  @Override
  public void handleMessage(MessageHeaders messageHeaders,
                            BatchUploadResponse response) {

    if (response.getSuccess()) {
      claimConnector.updateClaimStatusToAwaitingAgentApproval(response.getRequestId());
      log.info("RequestId {} was successful on DRS callback", response.getRequestId());
    } else {
      log.error("RequestId {} - Error occurred while processing claim. Error Message {}",
          response.getRequestId(), response.getErrorMessage());
      claimConnector.updateClaimStatusToDrsError(response.getRequestId());
    }
  }
}
