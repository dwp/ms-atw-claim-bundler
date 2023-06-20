package uk.gov.dwp.health.atw.msclaimbundler.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;
import uk.gov.dwp.health.integration.message.events.QueueEvent;

@Slf4j
public class DocumentBatchEvent extends QueueEvent {

  public DocumentBatchEvent(String queue, BatchUpload payload) {
    log.info("Creating DocumentBatchEvent to send to DRS. Queue {}",
        queue);
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    Map<String, Object> data = mapper.convertValue(payload, Map.class);
    setOutboundQueue(queue);
    setPayload(data);
  }
}
