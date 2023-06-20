package uk.gov.dwp.health.atw.msclaimbundler.services;

import org.springframework.stereotype.Service;
import uk.gov.dwp.health.atw.msclaimbundler.connectors.ContactInformationConnector;
import uk.gov.dwp.health.atw.msclaimbundler.models.request.ContactInformationRequest;
import uk.gov.dwp.health.atw.msclaimbundler.repository.S3Repository;

@Service
public class ContactInformationService {

  final ContactInformationConnector contactInformationConnector;
  final S3Repository s3Repository;

  public ContactInformationService(ContactInformationConnector contactInformationConnector,
                                   S3Repository s3Repository) {
    this.contactInformationConnector = contactInformationConnector;
    this.s3Repository = s3Repository;
  }

  public ContactInformationRequest getContactInformationForId(String id) {
    return contactInformationConnector.getContactInformationForId(id);
  }
}
