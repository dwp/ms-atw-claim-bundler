package uk.gov.dwp.health.atw.msclaimbundler.utils;

import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import uk.gov.dwp.health.atw.msclaimbundler.models.Evidence;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.Address;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.BankDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.Claimant;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.ContactInformation;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.NewPayeeDetails;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.Payee;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.PayeeDetails;
import uk.gov.dwp.health.atw.openapi.model.BatchUpload;
import uk.gov.dwp.health.atw.openapi.model.Envelope;
import uk.gov.dwp.health.atw.openapi.model.EnvelopeDocument;

public class TestData {
  public static final String NINO_8 = "AA370773";
  public static final String NINO_9 = "A";
  public static final String UUID_MOCK = "8d8b30e3-de52-4f1c-a71c-9905a8043dac";
  public static final String NINO = NINO_8 + NINO_9;
  public static final String ATW_NUMBER = "ATW1234567";
  public static final String POSTCODE = "NE26 4RS";
  public static final String FORENAME = "Odin";
  public static final String SURNAME = "Surtsson";
  public static final String EMAIL = "odin.surtsson@norse.com";
  public static final String HOME_NUMBER = "01200000000";
  public static final String MOBILE_NUMBER = "07500000000";
  public static final LocalDate DATE_OF_BIRTH = LocalDate.of(1994, 11, 22);
  public static final String COMPANY = "Company 1";
  public static final String AGENT_STAFF_ID = "843";
  public static final LocalDate DATE_OF_BIRTH_LOCAL_DATE = LocalDate.of(1994, 11, 22);
  public static final LocalDateTime CREATED_DATE = LocalDateTime.of(2022, Month.MARCH, 14, 10, 0);
  public static final Double DECLARATION_VERSION = 2.3;

  public static final Evidence evidence = Evidence.builder()
      .fileId("633ce73b-1414-433e-8a08-72449a0244fc/144b2aca-996d-4c27-bdf2-1e9b418874d3")
      .fileName("6b99f480c27e246fa5dd0453cd4fba29.pdf")
      .build();

  public static final List<Evidence> evidences = singletonList(evidence);

  public static final Address address = Address.builder()
      .address1("THE COTTAGE 1")
      .address3("WHITLEY BAY")
      .postcode(POSTCODE)
      .build();

  public static final Address newAddress = Address.builder()
      .address1("15 Redburry Grove")
      .address2("Bramhope")
      .address3("Leeds")
      .address4("West Yorkshire")
      .postcode("NE26 4RS")
      .build();

  public static final Claimant claimant = Claimant.builder()
      .dateOfBirth(DATE_OF_BIRTH)
      .surname(SURNAME)
      .forename(FORENAME)
      .homeNumber(HOME_NUMBER)
      .mobileNumber(MOBILE_NUMBER)
      .emailAddress(EMAIL)
      .address(address)
      .company(COMPANY)
      .build();

  public static final Claimant claimantWithNoDateOfBirth = Claimant.builder()
      .surname(SURNAME)
      .forename(FORENAME)
      .homeNumber(HOME_NUMBER)
      .mobileNumber(MOBILE_NUMBER)
      .emailAddress(EMAIL)
      .address(address)
      .company(COMPANY)
      .build();

  public static final PayeeDetails payeeDetails = PayeeDetails.builder()
      .fullName("Citizen One")
      .build();

  public static final PayeeDetails payeeDetailsFull = PayeeDetails.builder()
      .fullName("Citizen One")
      .emailAddress("citizen@email.com")
      .build();

  public static final BankDetails bankDetails = BankDetails.builder()
      .accountHolderName("Citizen One")
      .sortCode("000004")
      .accountNumber("12345677")
      .build();

  public static final BankDetails bankDetailsForExistingPayee = BankDetails.builder()
      .accountNumber("12345677")
      .build();

  public static final Payee newPayee = Payee.builder()
      .newPayee(true)
      .details(payeeDetails)
      .address(address)
      .bankDetails(bankDetails)
      .build();

  public static final Payee existingPayee = Payee.builder()
      .newPayee(false)
      .details(payeeDetails)
      .bankDetails(bankDetailsForExistingPayee)
      .build();

  public static final Payee existingPayeeOldDataModel = Payee.builder()
      .newPayee(false)
      .details(payeeDetailsFull)
      .build();

  public static final NewPayeeDetails newPayeeDetails = NewPayeeDetails.builder()
      .payee(newPayee)
      .accessToWorkNumber(ATW_NUMBER)
      .declarationVersion(DECLARATION_VERSION)
      .createdDate(CREATED_DATE)
      .currentContactInformation(ContactInformation.builder()
          .emailAddress(claimant.getEmailAddress())
          .forename(claimant.getForename())
          .surname(claimant.getSurname())
          .homeNumber(claimant.getHomeNumber())
          .mobileNumber(claimant.getMobileNumber())
          .address(claimant.getAddress())
          .build())
      .build();

  public static Envelope envelope() {
    Envelope envelope = new Envelope();
    envelope.ninoBody(NINO_8);
    envelope.ninoSuffix("");
    envelope.claimRef(ATW_NUMBER);
    envelope.dateOfBirth(claimant.getDateOfBirth());
    envelope.forename(claimant.getForename());
    envelope.surname(claimant.getSurname());
    envelope.postCode(claimant.getAddress().getPostcode());
    envelope.agentStaffId("843");
    return envelope;
  }

  public static Envelope envelopeWithClaimantNoDateOfBirth() {
    Envelope envelope = new Envelope();
    envelope.ninoBody(NINO_8);
    envelope.ninoSuffix("");
    envelope.claimRef(ATW_NUMBER);
    envelope.forename(claimant.getForename());
    envelope.surname(claimant.getSurname());
    envelope.postCode(claimant.getAddress().getPostcode());
    envelope.agentStaffId("843");
    return envelope;
  }

  public static List<EnvelopeDocument> listOfEnvelopeDocument(LocalDateTime createOn) {
    List<EnvelopeDocument> envelopeDocuments = new ArrayList<>();
    envelopeDocuments.add(
        envelopeDocument("https://s3.amazonaws.com/bucket/fileId1",
            createOn,
            DocumentType.INVOICE.id)
    );
    return envelopeDocuments;
  }

  public static EnvelopeDocument envelopeDocument(String documentUrl, LocalDateTime createOn,
                                                  int documentType) {
    return new EnvelopeDocument()
        .documentUrl(documentUrl)
        .documentDate(createOn)
        .documentType(documentType);
  }

  public static BatchUpload expectedBatchUploadWithEnvelopeDocument(LocalDateTime createOn,
                                                                    LinkedHashMap<String, DocumentType> documentDetails) {

    List<EnvelopeDocument> envelopeDocuments = listOfEnvelopeDocument(createOn);
    return createExpectedBatchUpload(createOn, documentDetails, envelopeDocuments);
  }

  public static BatchUpload expectedBatchUpload(LocalDateTime createOn,
                                                LinkedHashMap<String, DocumentType> documentDetails) {

    List<EnvelopeDocument> envelopeDocuments = new ArrayList<>();
    return createExpectedBatchUpload(createOn, documentDetails, envelopeDocuments);
  }

  private static BatchUpload createExpectedBatchUpload(LocalDateTime createOn,
                                                       LinkedHashMap<String, DocumentType> documentDetails,
                                                       List<EnvelopeDocument> envelopeDocuments) {

    documentDetails.entrySet().stream().map(documentUrl ->
        new EnvelopeDocument()
            .documentDate(createOn)
            .documentUrl(documentUrl.getKey())
            .documentType(documentUrl.getValue().id)
    ).forEach(envelopeDocuments::add);

    return new BatchUpload()
        .callerId("callerId")
        .requestId(UUID_MOCK)
        .responseRoutingKey("responseRoutingKey")
        .envelopes(List.of(new Envelope()
            .ninoBody(NINO_8)
            .ninoSuffix(NINO_9)
            .claimRef(ATW_NUMBER)
            .dateOfBirth(DATE_OF_BIRTH_LOCAL_DATE)
            .forename(FORENAME)
            .surname(SURNAME)
            .postCode(POSTCODE)
            .agentStaffId(AGENT_STAFF_ID)
            .documents(envelopeDocuments)));
  }
}
