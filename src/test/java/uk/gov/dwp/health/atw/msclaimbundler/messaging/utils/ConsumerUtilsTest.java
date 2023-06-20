package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.EQUIPMENT_OR_ADAPTATION;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.ATW_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.CREATED_DATE;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.DECLARATION_VERSION;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO_8;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.claimant;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.claimantWithNoDateOfBirth;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.envelopeDocument;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.evidences;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newPayee;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newPayeeDetails;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.MinimalClaimBody;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.msclaimbundler.utils.TestData;

@SpringBootTest(classes = ConsumerUtils.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerUtilsTest {

  @MockBean
  ClaimToPdfConnector claimToPdfConnector;

  @MockBean
  ContactInformationToPdfConnector contactInformationToPdfConnector;

  @MockBean
  S3Repository s3repository;

  FormGeneratorService formGeneratorService;

  MinimalClaimBody data;

  @BeforeAll
  void setup() {
    formGeneratorService = new FormGeneratorService(claimToPdfConnector,
        contactInformationToPdfConnector, s3repository);
    data = new MinimalClaimBody(
        NINO,
        ATW_NUMBER,
        EQUIPMENT_OR_ADAPTATION,
        claimant,
        CREATED_DATE,
        ClaimStatus.AWAITING_DRS_UPLOAD,
        evidences,
        newPayee,
        DECLARATION_VERSION
    );
  }

  @Test
  void getEnvelope() {
    assertEquals(TestData.envelope(),
        ConsumerUtils.getEnvelope(data.getAtwNumber(), NINO_8, claimant));
  }

  @Test
  void getEnvelopeWithClaimantNoDateOfBirth() {
    assertEquals(TestData.envelopeWithClaimantNoDateOfBirth(),
        ConsumerUtils.getEnvelope(data.getAtwNumber(), NINO_8, claimantWithNoDateOfBirth));
  }

  @Test
  void getNewPayeeEnvelopeDocument() throws MalformedURLException {

    String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";

    when(claimToPdfConnector.generateAndUploadNewPayeePdf(any()))
        .thenReturn(new ClaimToPdfResponse("fileId"));

    when(s3repository.getUrlForFileId(any()))
        .thenReturn(new URL(newPayeeForUrl));

    assertEquals(
        envelopeDocument(newPayeeForUrl, CREATED_DATE, DocumentType.EQUIPMENT_OR_ADAPTATIONS.id),
        ConsumerUtils.getNewPayeeEnvelopeDocument("request", data, EQUIPMENT_OR_ADAPTATION,
            claimant, formGeneratorService));
  }

  @Test
  void getNewPayeeDetails() {
    assertEquals(newPayeeDetails, ConsumerUtils.getNewPayeeDetails(data, claimant));
  }

  @Test
  void getEnvelopeDocument() throws MalformedURLException {
    String newPayeeForUrl = "http://localhost:8080/newPayee/newPayee/NP1234567";

    assertEquals(
        envelopeDocument(newPayeeForUrl, CREATED_DATE, DocumentType.EQUIPMENT_OR_ADAPTATIONS.id),
        ConsumerUtils.getEnvelopeDocument(new URL(newPayeeForUrl), data.getCreatedDate(),
            EQUIPMENT_OR_ADAPTATION));
  }
}