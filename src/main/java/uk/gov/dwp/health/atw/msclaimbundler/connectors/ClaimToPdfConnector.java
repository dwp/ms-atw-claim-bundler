package uk.gov.dwp.health.atw.msclaimbundler.connectors;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;

@Service
public class ClaimToPdfConnector {
  @Value("${service.ms-claim-to-pdf.uri}")
  private String serviceUri;

  final RestTemplate restTemplate;

  public ClaimToPdfConnector(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public ClaimToPdfResponse generateAndUploadClaimPdf(Map<String, Object> claimData) {
    HttpHeaders mainRequestHeader = new HttpHeaders();
    mainRequestHeader.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request =
        new HttpEntity<>(claimData, mainRequestHeader);

    return restTemplate.postForObject(serviceUri + "/generate/claim-form", request,
        ClaimToPdfResponse.class);
  }

  public ClaimToPdfResponse generateAndUploadNewPayeePdf(NewPayeeDetails claimData) {
    HttpHeaders mainRequestHeader = new HttpHeaders();
    mainRequestHeader.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<NewPayeeDetails> request =
        new HttpEntity<>(claimData, mainRequestHeader);

    return restTemplate.postForObject(serviceUri + "/generate/create-payee", request,
        ClaimToPdfResponse.class);
  }
}
