package uk.gov.dwp.health.atw.msclaimbundler.messaging.utils;

import java.net.URL;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.Claimant;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.ContactInformation;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.MinimalClaimBody;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.services.FormGeneratorService;
import uk.gov.dwp.health.atw.openapi.model.Envelope;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;

@Slf4j
public class ConsumerUtils {

  public static Envelope getEnvelope(String atwNumber, String nino,
                                     Claimant claimant) {
    Envelope envelope = new Envelope();
    envelopDetails(atwNumber, nino, envelope);
    envelope.dateOfBirth(claimant.getDateOfBirth());  // (DiSC)
    envelope.forename(DrsCleansingUtils.validateAndCleanseNameForDrs(
        claimant.getForename()));  // (DiSC)
    envelope.surname(DrsCleansingUtils.validateAndCleanseNameForDrs(
        claimant.getSurname()));  // (DiSC)
    envelope.postCode(DrsCleansingUtils.validateAndCleansePostcodeForDrs(
        claimant.getAddress().getPostcode()));  // (DiSC)
    return envelope;
  }

  public static Envelope getEnvelope(String atwNumber, String nino,
                                     ContactInformation contactInformation) {
    Envelope envelope = new Envelope();
    envelopDetails(atwNumber, nino, envelope);
    envelope.dateOfBirth(contactInformation.getDateOfBirth());  // (DiSC)
    envelope.forename(DrsCleansingUtils.validateAndCleanseNameForDrs(
        contactInformation.getForename()));  // (DiSC)
    envelope.surname(DrsCleansingUtils.validateAndCleanseNameForDrs(
        contactInformation.getSurname()));  // (DiSC)
    envelope.postCode(DrsCleansingUtils.validateAndCleansePostcodeForDrs(
        contactInformation.getAddress().getPostcode()));  // (DiSC)
    return envelope;
  }

  private static void envelopDetails(String atwNumber, String nino, Envelope envelope) {
    envelope.ninoBody(nino.substring(0, 8));
    envelope.ninoSuffix(
        nino.length() > 8 ? nino.substring(8, 9) : "");
    envelope.claimRef(atwNumber); // AtW Number (DiSC)
    envelope.agentStaffId("843");
  }


  public static EnvelopeDocument getNewPayeeEnvelopeDocument(
      String requestId, MinimalClaimBody minimalClaimBody, ClaimType claimType, Claimant claimant,
      FormGeneratorService formGeneratorService) {

    NewPayeeDetails newPayeeDetails = getNewPayeeDetails(minimalClaimBody, claimant);

    // Send Claim data to produce the PDF and get URIs
    URL newPayeeFormS3Url =
        formGeneratorService.generateAndUploadNewPayeePdf(newPayeeDetails);
    log.info("{} newPayeeFormS3Url {}", requestId, newPayeeFormS3Url);

    return getEnvelopeDocument(newPayeeFormS3Url, minimalClaimBody.getCreatedDate(), claimType);
  }

  public static NewPayeeDetails getNewPayeeDetails(MinimalClaimBody minimalClaimBody,
                                                   Claimant claimant) {
    return NewPayeeDetails.builder()
        .payee(minimalClaimBody.getPayee())
        .accessToWorkNumber(minimalClaimBody.getAtwNumber())
        .declarationVersion(minimalClaimBody.getDeclarationVersion())
        .createdDate(minimalClaimBody.getCreatedDate())
        .currentContactInformation(ContactInformation.builder()
            .emailAddress(claimant.getEmailAddress())
            .forename(claimant.getForename())
            .surname(claimant.getSurname())
            .homeNumber(claimant.getHomeNumber())
            .mobileNumber(claimant.getMobileNumber())
            .address(claimant.getAddress())
            .build())
        .build();
  }

  public static EnvelopeDocument getEnvelopeDocument(URL formS3Url,
                                                     LocalDateTime createdDate,
                                                     ClaimType claimType) {
    EnvelopeDocument envelopeDocument = new EnvelopeDocument();
    envelopeDocument.documentUrl(formS3Url.toString());
    envelopeDocument.documentDate(LocalDateTime.parse(
        createdDate.toString()));
    envelopeDocument.documentType(claimType.formConfig.getDrsDocumentId());
    return envelopeDocument;
  }
}
