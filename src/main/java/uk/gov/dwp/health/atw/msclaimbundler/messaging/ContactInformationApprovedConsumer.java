package uk.gov.dwp.health.atw.msclaimbundler.messaging;

import static java.util.Collections.singletonList;
import static uk.gov.dwp.health.atw.msclaimbundler.messaging.utils.ConsumerUtils.getEnvelope;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.ContactInformationStatus.AWAITING_UPLOAD;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.NEW_OR_AMENDED_DETAILS;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.DocumentBatchEvent;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactRetrievalRequest;
import uk.gov.dwp.health.atw.msclaimbundler.services.ContactInformationService;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;
import uk.gov.dwp.health.atw.openapi.model.Envelope;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;
import uk.gov.dwp.health.integration.message.consumers.HealthMessageConsumer;

@Slf4j
@Service
public class ContactInformationApprovedConsumer
    implements HealthMessageConsumer<ContactRetrievalRequest> {

  final ContactInformationService contactInformationService;

  final ContactInformationConnector contactInformationConnector;

  final FormGeneratorService formGeneratorService;

  final SnsPublisher snsPublisher;

  @Value("${service.consumer.update-contact.queue}")
  String queue;

  @Value("${service.consumer.update-contact.routingKey}")
  String routingKey;

  @Value("${document-batch.queue-name}")
  private String drsQueueName;

  @Value("${document-batch.callerId}")
  private String callerId;

  @Value("${document-batch.responseRoutingKey.contact}")
  private String responseRoutingKey;

  public ContactInformationApprovedConsumer(ContactInformationService contactInformationService,
                                            ContactInformationConnector contactInformationConnector,
                                            SnsPublisher snsPublisher,
                                            FormGeneratorService formGeneratorService) {
    this.contactInformationService = contactInformationService;
    this.contactInformationConnector = contactInformationConnector;
    this.snsPublisher = snsPublisher;
    this.formGeneratorService = formGeneratorService;
  }

  @Override
  public String getQueueName() {
    return queue;
  }

  @Override
  public String getRoutingKey() {
    return routingKey;
  }

  @SneakyThrows
  @Override
  public void handleMessage(MessageHeaders messageHeaders,
                            ContactRetrievalRequest contactRetrievalRequest) {
    String correlationId = UUID.randomUUID().toString();

    log.info("CorrelationId {} - Searching for {}", correlationId,
        contactRetrievalRequest.getId());

    //retrieve contact information to be updated
    ContactInformationRequest contactInformationData =
        contactInformationService.getContactInformationForId(
            contactRetrievalRequest.getId());
    log.info("CorrelationId {} - Contact information ID is {}", correlationId,
        contactInformationData.getId());
    log.info("CorrelationId {} - Access To Work Number is {}", correlationId,
        contactInformationData.getAccessToWorkNumber());


    if (contactInformationData.getContactInformationStatus() == AWAITING_UPLOAD) {
      contactInformationConnector.updateContactInformationStatusToProcessingUpload(
          contactInformationData.getId());
      log.info("CorrelationId {} - Contact information Status updated to Processing Upload",
          correlationId);

      //send the contact information to produce the pdf and get the URI
      URL contactInfoFormS3Url =
          formGeneratorService.generateAndUploadUpdateContactInformationPdf(contactInformationData);
      log.info("CorrelationId {} - contactInformationFormS3Url {}", correlationId,
          contactInfoFormS3Url);

      //create the envelope document for the document
      EnvelopeDocument updateContactInformationForm = new EnvelopeDocument();
      updateContactInformationForm.documentUrl(contactInfoFormS3Url.toString());
      updateContactInformationForm.documentDate(
          LocalDateTime.parse(contactInformationData.getCreatedDate().toString()));
      updateContactInformationForm.documentType(NEW_OR_AMENDED_DETAILS.id);

      //create the envelope
      Envelope envelope = getEnvelope(contactInformationData.getAccessToWorkNumber(),
          contactInformationData.getNino(), contactInformationData.getCurrentContactInformation());

      //add the envelope document to the envelope
      envelope.documents(singletonList(updateContactInformationForm));
      BatchUpload batchUpload = new BatchUpload();
      batchUpload.callerId(callerId);
      batchUpload.correlationId(correlationId);
      batchUpload.requestId(contactInformationData.getId());
      batchUpload.responseRoutingKey(responseRoutingKey);


      //add the envelope to the batch upload
      batchUpload.addEnvelopesItem(envelope);

      //publish the document upload to drs
      snsPublisher.publishToDrs(new DocumentBatchEvent(drsQueueName, batchUpload));

      contactInformationConnector.updateContactInformationStatusToUploadedToDocumentBatch(
          contactInformationData.getId());
      log.info("CorrelationId {} - Contact information Status updated to Completed Upload",
          correlationId);
    } else {
      log.error(
          "CorrelationId {} - Contact Information status is not AWAITING_UPLOAD for "
              + "contact record {}, it was {}", correlationId, contactInformationData.getId(),
          contactInformationData.getContactInformationStatus());
    }
  }
}
