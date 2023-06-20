package uk.gov.dwp.health.atw.msclaimbundler.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.CONTACT_INFORMATION_ID;
import static uk.gov.dwp.health.atw.msclaimbundler.utils.ContactInformationTestData.awaitingUploadContactInformationRequest;

@SpringBootTest(classes = ContactInformationService.class)
class ContactInformationServiceTest {

  @Autowired
  private ContactInformationService contactInformationService;

  @MockBean
  ContactInformationConnector contactInformationConnector;

  @MockBean
  S3Repository s3Repository;

  @Test
  @DisplayName("success getContactInformationForIdAndAtwNumber")
  void getContactInformationForIdAndAtwNumber() {

    when(contactInformationConnector.getContactInformationForId(anyString()))
        .thenReturn(awaitingUploadContactInformationRequest);

    assertEquals(awaitingUploadContactInformationRequest, contactInformationService.getContactInformationForId(CONTACT_INFORMATION_ID));

    verify(contactInformationConnector, times(1)).getContactInformationForId(CONTACT_INFORMATION_ID);

  }

}