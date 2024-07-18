package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;
import uk.gov.dwp.health.integration.message.consumers.HealthMessageConsumer;

@Slf4j
@Service
public class ContactInformationApprovedResponseConsumer implements
    HealthMessageConsumer<BatchUploadResponse> {

  final ContactInformationConnector contactInformationConnector;

  @Value("${service.consumer.update-contact-response.queue}")
  String queue;

  @Value("${document-batch.responseRoutingKey.contact}")
  String routingKey;

  public ContactInformationApprovedResponseConsumer(
      ContactInformationConnector contactInformationConnector
  ) {
    this.contactInformationConnector = contactInformationConnector;
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
      contactInformationConnector.updateStatusToCompleted(response.getRequestId());
      log.info("RequestId {} - successful on DRS callback", response.getRequestId());
    } else {
      log.error("RequestId {} - Error occurred while processing claim. Error Message {}",
          response.getRequestId(), response.getErrorMessage());
      contactInformationConnector.updateStatusToDrsError(response.getRequestId());
    }
  }
}
