package uk.gov.dwp.health.atw.msclaimbundler.utils;

import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.ATW_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.CREATED_DATE;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.DATE_OF_BIRTH;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.DECLARATION_VERSION;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.EMAIL;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.FORENAME;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.HOME_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.MOBILE_NUMBER;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.NINO;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.SURNAME;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.address;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.TestData.newAddress;

import java.util.UUID;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ContactInformationStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.ContactInformation;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;

public class ContactInformationTestData {

  public static final String CONTACT_INFORMATION_ID = UUID.randomUUID().toString();

  public static final ContactInformation currentContactInformation = ContactInformation.builder()
      .forename(FORENAME)
      .surname(SURNAME)
      .dateOfBirth(DATE_OF_BIRTH)
      .emailAddress(MOBILE_NUMBER)
      .homeNumber(EMAIL)
      .address(address)
      .build();

  public static final ContactInformation newContactInformation =
      ContactInformation.builder()
          .forename(FORENAME)
          .surname(SURNAME)
          .dateOfBirth(DATE_OF_BIRTH)
          .emailAddress(EMAIL)
          .homeNumber(HOME_NUMBER)
          .mobileNumber(MOBILE_NUMBER)
          .address(newAddress)
          .build();

  public static final ContactInformationRequest awaitingUploadContactInformationRequest =
      ContactInformationRequest.builder()
          .id(CONTACT_INFORMATION_ID)
          .accessToWorkNumber(ATW_NUMBER)
          .nino(NINO)
          .declarationVersion(DECLARATION_VERSION)
          .currentContactInformation(currentContactInformation)
          .newContactInformation(newContactInformation)
          .createdDate(CREATED_DATE)
          .contactInformationStatus(ContactInformationStatus.AWAITING_UPLOAD)
          .build();
}
