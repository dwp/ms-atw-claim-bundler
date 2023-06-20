package uk.gov.dwp.health.atw.msclaimbundler.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;
import uk.gov.dwp.health.integration.message.events.QueueEvent;

@Slf4j
public class DocumentBatchResponseEvent extends QueueEvent {

  public DocumentBatchResponseEvent(String drsQueue, BatchUploadResponse payload) {
    log.info("Creating DocumentBatchResponseEvent response from DRS.");
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    Map<String, Object> data = mapper.convertValue(payload, Map.class);
    setOutboundQueue(drsQueue);
    setPayload(data);
  }
}
