package uk.gov.dwp.health.atw.msclaimbundler.services;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.Evidence;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;

@SpringBootTest(classes = ClaimService.class)
class ClaimServiceTests {

  @Autowired
  private ClaimService claimService;

  @MockBean
  ClaimConnector claimConnector;
  @MockBean
  S3Repository s3Repository;

  @Test
  @DisplayName("success getClaimForClaimReferenceAndNino")
  void getClaimForClaimReferenceAndNino() {
    Map<String, Object> response = Map.of("claimReference", "EA12314");
    when(claimConnector.getClaimForClaimReference(any(), any())).thenReturn(response);

    assertEquals(response, claimService.getClaimForClaimReferenceAndNino("EA12314", NINO));
    verify(claimConnector, times(1)).getClaimForClaimReference("EA12314", NINO);
  }


  @Test
  @DisplayName("getEvidenceEnvelopDocumentsFromClaimData = no evidence")
  void getEvidenceEnvelopDocumentsFromClaimDataNoEvidence() {
    LocalDateTime now = LocalDateTime.now();

    assertEquals(new ArrayList<EnvelopeDocument>(),
        claimService.getEvidenceEnvelopDocumentsFromClaimData(new ArrayList<Evidence>(), now));

    verify(s3Repository, times(0)).getUrlForFileId(any());
  }

  @Test
  @DisplayName("getEvidenceEnvelopDocumentsFromClaimData = has evidence")
  void getEvidenceEnvelopDocumentsFromClaimDataWithEvidence() throws MalformedURLException {
    LocalDateTime now = LocalDateTime.now();

    List<Evidence> evidenceList =
        List.of(new Evidence("fileId1", "fileName1"), new Evidence("fileId2", "fileName2"));

    when(s3Repository.getUrlForFileId("fileId1")).thenReturn(
        new URL("https://s3.amazonaws.com/bucket/fileId1"));
    when(s3Repository.getUrlForFileId("fileId2")).thenReturn(
        new URL("https://s3.amazonaws.com/bucket/fileId2"));

    List<EnvelopeDocument> expected = List.of(
        new EnvelopeDocument()
            .documentUrl("https://s3.amazonaws.com/bucket/fileId1")
            .documentDate(now)
            .documentType(12075),
        new EnvelopeDocument()
            .documentUrl("https://s3.amazonaws.com/bucket/fileId2")
            .documentDate(now)
            .documentType(12075)
    );

    List<EnvelopeDocument> actual =
        claimService.getEvidenceEnvelopDocumentsFromClaimData(evidenceList, now);

    assertEquals(expected.size(), actual.size());

    assertThat(actual.get(0), samePropertyValuesAs(expected.get(0)));
    assertThat(actual.get(1), samePropertyValuesAs(expected.get(1)));

    verify(s3Repository, times(1)).getUrlForFileId("fileId1");
    verify(s3Repository, times(1)).getUrlForFileId("fileId2");
  }


}
