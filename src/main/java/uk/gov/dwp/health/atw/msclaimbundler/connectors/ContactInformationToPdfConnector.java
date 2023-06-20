package uk.gov.dwp.health.atw.msclaimbundler.connectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;

@Service
public class ContactInformationToPdfConnector {
  @Value("${service.ms-claim-to-pdf.uri}")
  private String serviceUri;

  final RestTemplate restTemplate;

  public ContactInformationToPdfConnector(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ClaimToPdfResponse generateAndUploadUpdateContactInformationPdf(
      ContactInformationRequest contactInformationData) {

    HttpHeaders mainRequestHeader = new HttpHeaders();
    mainRequestHeader.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ContactInformationRequest> request =
        new HttpEntity<>(contactInformationData, mainRequestHeader);

    return restTemplate.postForObject(serviceUri + "/generate/update-contact-details", request,
        ClaimToPdfResponse.class);
  }
}
