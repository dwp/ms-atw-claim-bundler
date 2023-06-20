package uk.gov.dwp.health.atw.msclaimbundler.services;

import java.net.URL;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationToPdfConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.models.response.ClaimToPdfResponse;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;

@Service
public class FormGeneratorService {

  final ClaimToPdfConnector claimToPdfConnector;

  final ContactInformationToPdfConnector contactInformationToPdfConnector;

  final S3Repository s3repository;

  public FormGeneratorService(ClaimToPdfConnector claimToPdfConnector,
                              ContactInformationToPdfConnector contactInformationToPdfConnector,
                              S3Repository s3repository) {
    this.claimToPdfConnector = claimToPdfConnector;
    this.contactInformationToPdfConnector = contactInformationToPdfConnector;
    this.s3repository = s3repository;
  }

  public URL generateAndUploadClaimPdf(Map<String, Object> data) {
    ClaimToPdfResponse response = claimToPdfConnector.generateAndUploadClaimPdf(data);
    return s3repository.getUrlForFileId(response.getFileId());
  }

  public URL generateAndUploadNewPayeePdf(NewPayeeDetails data) {
    ClaimToPdfResponse response = claimToPdfConnector.generateAndUploadNewPayeePdf(data);
    return s3repository.getUrlForFileId(response.getFileId());
  }

  public URL generateAndUploadUpdateContactInformationPdf(ContactInformationRequest data) {
    ClaimToPdfResponse response =
        contactInformationToPdfConnector.generateAndUploadUpdateContactInformationPdf(data);
    return s3repository.getUrlForFileId(response.getFileId());
  }
}
