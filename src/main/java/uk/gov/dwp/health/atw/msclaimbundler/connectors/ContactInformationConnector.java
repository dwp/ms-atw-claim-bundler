package uk.gov.dwp.health.atw.msclaimbundler.connectors;


import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;

@Service
public class ContactInformationConnector {

  @Value("${service.ms-claim.uri}")
  String serviceUri;

  final RestTemplate restTemplate;

  public ContactInformationConnector(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ContactInformationRequest getContactInformationForId(String id) {
    return restTemplate.postForObject(serviceUri + "/retrieve-contact-information",
        Map.of("requestId", id), ContactInformationRequest.class);
  }

  public void updateContactInformationStatusToProcessingUpload(String id) {
    restTemplate.put(serviceUri + "/contact/change-status/processing-upload",
        Map.of("requestId", id));
  }

  public void updateContactInformationStatusToUploadedToDocumentBatch(String id) {
    restTemplate.put(serviceUri + "/contact/change-status/uploaded-to-document-batch",
        Map.of("requestId", id));
  }

  public void updateStatusToCompleted(String requestId) {
    restTemplate.put(serviceUri + "/contact/change-status/completed-upload",
        Map.of("requestId", requestId));
  }

  public void updateStatusToDrsError(String requestId) {
    restTemplate.put(serviceUri + "/contact/change-status/failed-drs-upload",
        Map.of("requestId", requestId));
  }
}
