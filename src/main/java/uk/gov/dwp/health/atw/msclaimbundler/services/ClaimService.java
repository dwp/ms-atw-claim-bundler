package uk.gov.dwp.health.atw.msclaimbundler.services;

import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.INVOICE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.Evidence;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;

@Service
public class ClaimService {

  final ClaimConnector claimConnector;
  final S3Repository s3Repository;

  public ClaimService(ClaimConnector claimConnector, S3Repository s3Repository) {
    this.claimConnector = claimConnector;
    this.s3Repository = s3Repository;
  }

  public Map<String, Object> getClaimForClaimReferenceAndNino(String claimReference, String nino) {
    return claimConnector.getClaimForClaimReference(
        claimReference, nino);
  }

  public List<EnvelopeDocument> getEvidenceEnvelopDocumentsFromClaimData(
      List<Evidence> claimEvidence, LocalDateTime createdOn) {
    return claimEvidence.stream().map(evidence -> {
      EnvelopeDocument envelopeDocument = new EnvelopeDocument();
      envelopeDocument.documentDate(createdOn);
      envelopeDocument.documentUrl(s3Repository.getUrlForFileId(evidence.getFileId()).toString());
      envelopeDocument.documentType(INVOICE.id);
      return envelopeDocument;
    }).collect(Collectors.toList());
  }

}
