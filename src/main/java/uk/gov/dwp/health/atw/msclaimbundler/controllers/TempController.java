package uk.gov.dwp.health.atw.msclaimbundler.controllers;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dwp.health.atw.msclaimbundler.messaging.SnsPublisher;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchResponseEvent;
import uk.gov.dwp.health.atw.openapi.model.BatchUploadResponse;

@RestController
@Validated
public class TempController {

  @Value("${document-batch.queue-name}")
  private String drsResponseQueue;

  @Value("${document-batch.responseRoutingKey.contact}")
  private String drsResponseRoutingKeyContact;


  final SnsPublisher snsPublisher;

  public TempController(SnsPublisher snsPublisher) {
    this.snsPublisher = snsPublisher;
  }

  @GetMapping(value = "/generate/{nino}/{claimId}")
  public ResponseEntity<?> submitClaim(@PathVariable String nino, @PathVariable String claimId) {
    snsPublisher.sendTestMessage(Map.of("claimReference", claimId, "nino", nino));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/drs-response/claim")
  public ResponseEntity<?> createDrsResponseClaim(@RequestBody BatchUploadResponse request) {
    snsPublisher.sendDrsResponse(
        new DocumentBatchResponseEvent(drsResponseQueue, request));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/drs-response/contact")
  public ResponseEntity<?> createDrsResponseContact(@RequestBody BatchUploadResponse request) {
    snsPublisher.sendDrsResponse(
        new DocumentBatchResponseEvent(drsResponseQueue, request));
    return ResponseEntity.ok().build();
  }
}
