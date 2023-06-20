package uk.gov.dwp.health.atw.msclaimbundler.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.awaitingUploadContactInformationRequest;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newPayeeDetails;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;

@SpringBootTest(classes = FormGeneratorService.class)
class FormGeneratorServiceTests {

  @Autowired
  private FormGeneratorService formGeneratorService;

  @MockBean
  private ClaimToPdfConnector claimToPdfConnector;

  @MockBean
  private ContactInformationToPdfConnector contactInformationToPdfConnector;

  @MockBean
  private S3Repository s3repository;

  @Test
  @DisplayName("generateAndUploadClaimPdf successful")
  void generateAndUploadClaimPdf() throws MalformedURLException {
    final Map<String, Object> claim = Map.of("claim", "data");
    final String fileId = UUID.randomUUID().toString();
    final String url = "https://s3.com/file.pdf";

    when(claimToPdfConnector.generateAndUploadClaimPdf(any(Map.class))).thenReturn(
        new ClaimToPdfResponse(fileId));

    when(s3repository.getUrlForFileId(fileId)).thenReturn(new URL(url));

    assertEquals(url,
        formGeneratorService.generateAndUploadClaimPdf(claim).toString());

    verify(s3repository, times(1)).getUrlForFileId(fileId);
  }

  @Test
  @DisplayName("generateAndUploadNewPayeePdf successful")
  void generateAndUploadNewPayeePdf() throws MalformedURLException {
    final String fileId = UUID.randomUUID().toString();
    final String url = "https://s3.com/file.pdf";

    when(claimToPdfConnector.generateAndUploadNewPayeePdf(any(NewPayeeDetails.class)))
        .thenReturn(new ClaimToPdfResponse(fileId));

    when(s3repository.getUrlForFileId(fileId)).thenReturn(new URL(url));

    assertEquals(url,
        formGeneratorService.generateAndUploadNewPayeePdf(newPayeeDetails).toString());

    verify(s3repository, times(1)).getUrlForFileId(fileId);
  }

  @Test
  @DisplayName("generateAndUploadUpdateContactInformationPdf successful")
  void generateAndUploadUpdateContactInformationPdf() throws MalformedURLException {
    final String fileId = UUID.randomUUID().toString();
    final String url = "https://s3.com/file.pdf";

    when(contactInformationToPdfConnector.generateAndUploadUpdateContactInformationPdf(
        any(ContactInformationRequest.class)))
        .thenReturn(new ClaimToPdfResponse(fileId));

    when(s3repository.getUrlForFileId(fileId)).thenReturn(new URL(url));

    assertEquals(url, formGeneratorService.generateAndUploadUpdateContactInformationPdf(
        awaitingUploadContactInformationRequest).toString());

    verify(s3repository, times(1)).getUrlForFileId(fileId);
  }
}
