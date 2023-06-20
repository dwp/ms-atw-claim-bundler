package uk.gov.dwp.health.atw.msclaimbundler.models.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@ToString
public abstract class ClaimException extends Exception {
  private HttpStatus errorCode;
  private String errorMessage;
}
