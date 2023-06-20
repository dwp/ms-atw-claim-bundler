package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchResponseEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.TestInboundClaimReferenceMessage;
import uk.gov.dwp.health.integration.message.events.EventManager;
import uk.gov.dwp.health.integration.message.events.QueueEventManager;

@Slf4j
@Component
public class SnsPublisher {

  final EventManager eventManager;

  final QueueEventManager queueEventManager;

  public SnsPublisher(EventManager eventManager, QueueEventManager queueEventManager) {
    this.eventManager = eventManager;
    this.queueEventManager = queueEventManager;
  }

  public void sendTestMessage(Map<String, Object> payload) {
    eventManager.send(new TestInboundClaimReferenceMessage(payload));
    log.info("Claim reference sent");
  }

  public void sendDrsResponse(DocumentBatchResponseEvent payload) {
    queueEventManager.send(payload);
    log.info("Response from DRS");
  }

  public void publishToDrs(DocumentBatchEvent payload) {
    queueEventManager.send(payload);
    log.info("Sent to DRS");
  }
}
