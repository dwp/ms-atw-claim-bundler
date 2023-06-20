package uk.gov.dwp.health.atw.msclaimbundler.models;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.dwp.health.integration.message.events.Event;

public class TestInboundClaimReferenceMessage extends Event {

  @Value("${service.consumer.topic}")
  private String topic;

  @Value("${service.consumer.new-claim.routingKey}")
  String routingKey;

  public TestInboundClaimReferenceMessage(final Map<String, Object> payload) {
    setRoutingKey(routingKey);
    setTopic(topic);
    setPayload(payload);
  }
}
