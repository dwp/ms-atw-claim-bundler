package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils.getEnvelope;
import static uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils.getEnvelopeDocument;
import static uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils.getNewPayeeEnvelopeDocument;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType.NEW_OR_AMENDED_DETAILS;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ClaimConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.Claimant;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.MinimalClaimBody;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ClaimReferenceNinoRequest;
import uk.gov.dwp.health.atw.msclaimbundler.services.ClaimService;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;
import uk.gov.dwp.health.atw.openapi.model.Envelope;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;
import uk.gov.dwp.health.integration.message.consumers.HealthMessageConsumer;

@Slf4j
@Service
public class ClaimApprovedConsumer implements HealthMessageConsumer<ClaimReferenceNinoRequest> {

  final ClaimService claimService;

  final ClaimConnector claimConnector;

  final FormGeneratorService formGeneratorService;

  final SnsPublisher snsPublisher;

  @Value("${service.consumer.new-claim.queue}")
  String queue;

  @Value("${service.consumer.new-claim.routingKey}")
  String routingKey;

  @Value("${document-batch.queue-name}")
  private String drsQueueName;

  @Value("${document-batch.callerId}")
  private String callerId;

  @Value("${document-batch.responseRoutingKey.claim}")
  private String responseRoutingKey;

  public ClaimApprovedConsumer(ClaimService claimService, ClaimConnector claimConnector,
                               SnsPublisher snsPublisher,
                               FormGeneratorService formGeneratorService) {
    this.claimService = claimService;
    this.claimConnector = claimConnector;
    this.snsPublisher = snsPublisher;
    this.formGeneratorService = formGeneratorService;
  }

  @Override
  public String getQueueName() {
    return queue;
  }

  @SneakyThrows
  @Override
  public void handleMessage(MessageHeaders messageHeaders,
                            ClaimReferenceNinoRequest claimReferenceRequest) {

    String requestId = UUID.randomUUID().toString();

    log.info("RequestId {} - Searching for {}", requestId,
        claimReferenceRequest.getClaimReference());

    // Retrieve the full claim from ms-claim
    Map<String, Object> claimData =
        claimService.getClaimForClaimReferenceAndNino(claimReferenceRequest.getClaimReference(),
            claimReferenceRequest.getNino());

    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    MinimalClaimBody minimalClaimBody = mapper.convertValue(claimData, MinimalClaimBody.class);

    if (minimalClaimBody.getClaimStatus() == ClaimStatus.AWAITING_DRS_UPLOAD) {

      ClaimType claimType = minimalClaimBody.getClaimType();

      log.info("RequestId {} - Claim Type is {}", requestId, claimType);
      log.info("RequestId {} - AtwForm is {}", requestId, claimType.formConfig.getAtwFormId());
      log.info("RequestId {} - DRSForm is {}", requestId, claimType.formConfig.getDrsDocumentId());


      // Send Claim data to produce the PDF and get URIs
      URL claimFormS3Url = formGeneratorService.generateAndUploadClaimPdf(claimData);
      log.info("RequestId {} - claimFormS3Url {}", requestId, claimFormS3Url);

      EnvelopeDocument claimForm =
          getEnvelopeDocument(claimFormS3Url, minimalClaimBody.getCreatedDate(), claimType);

      List<EnvelopeDocument> envelopeDocument = new ArrayList<>();

      if (minimalClaimBody.getEvidence() != null && !minimalClaimBody.getEvidence().isEmpty()) {
        envelopeDocument.addAll(
            claimService.getEvidenceEnvelopDocumentsFromClaimData(minimalClaimBody.getEvidence(),
                minimalClaimBody.getCreatedDate()));
      } else {
        log.info("RequestId {} - No Evidence found for claimReference {}", requestId,
            claimReferenceRequest.getClaimReference());
      }

      String nino = minimalClaimBody.getNino();
      Claimant claimant = minimalClaimBody.getClaimant();

      // Add claim form to List of Envelope Documents
      envelopeDocument.add(claimForm);

      if (minimalClaimBody.getPayee().isNewPayee()) {
        log.info("RequestId {} - Creating new payee form", requestId);
        EnvelopeDocument newPayeeForm =
            getNewPayeeEnvelopeDocument(requestId, minimalClaimBody, NEW_OR_AMENDED_DETAILS,
                claimant, formGeneratorService);

        //add new payee form to List of Envelope Documents
        envelopeDocument.add(newPayeeForm);
      }

      Envelope envelope = getEnvelope(minimalClaimBody.getAtwNumber(), nino, claimant);

      // Add List of Envelope Documents to Envelope
      envelope.documents(envelopeDocument);

      BatchUpload batchUpload = new BatchUpload();
      batchUpload.callerId(callerId);
      batchUpload.requestId(requestId);
      batchUpload.responseRoutingKey(responseRoutingKey);

      // Add Envelope to Batch Upload
      batchUpload.addEnvelopesItem(envelope);

      // Send Batch Upload to DRS
      snsPublisher.publishToDrs(new DocumentBatchEvent(drsQueueName, batchUpload));

      // Update claim status to mark as Uploaded to DRS
      claimConnector.updateClaimStatusToUploadedToDocumentBatch(
          claimReferenceRequest.getClaimReference(), requestId);
      log.info("RequestId {} - {} claim status updated to Uploaded to Document Batch", requestId,
          claimType);

      log.info("RequestId {} - claim {} done", requestId, claimType);
    } else {
      log.error(
          "RequestId {} - Claim Status is not AWAITING_DRS_UPLOAD for claim reference {}, "
              + "it was {}",
          requestId, claimReferenceRequest.getClaimReference(), minimalClaimBody.getClaimStatus());
    }
  }
}
