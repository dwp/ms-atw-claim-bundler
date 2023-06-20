package uk.gov.dwp.health.atw.msclaimbundler.models.exceptions;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class InvalidClaimReferenceFormat extends ClaimException {

  public InvalidClaimReferenceFormat(@NonNull String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
