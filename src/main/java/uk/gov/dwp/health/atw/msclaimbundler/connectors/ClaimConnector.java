package uk.gov.dwp.health.atw.msclaimbundler.connectors;


import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClaimConnector {

  @Value("${service.ms-claim.uri}")
  String serviceUri;

  final RestTemplate restTemplate;

  public ClaimConnector(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Map<String, Object> getClaimForClaimReference(String claimReference, String nino) {
    return restTemplate.postForObject(serviceUri + "/claim-for-reference-and-nino",
        Map.of("claimReference", claimReference.toUpperCase(), "nino", nino), Map.class);
  }

  public void updateClaimStatusToUploadedToDocumentBatch(String claimReference, String requestId) {
    restTemplate.put(serviceUri + "/change-status/uploaded-to-document-batch",
        Map.of("claimReference", claimReference.toUpperCase(), "requestId",
            requestId));
  }

  public void updateClaimStatusToDrsError(String requestId) {
    restTemplate.put(serviceUri + "/change-status/drs-error",
        Map.of("requestId",
            requestId));
  }

  public void updateClaimStatusToAwaitingAgentApproval(String requestId) {
    restTemplate.put(serviceUri + "/change-status/awaiting-agent-approval",
        Map.of("requestId",
            requestId));
  }

}
