package uk.gov.dwp.health.atw.msclaimbundler.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType;
import uk.gov.dwp.health.atw.msclaimbundler.models.exceptions.InvalidClaimReferenceFormat;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimReferenceRequest {

  @JsonProperty(value = "claimReference")
  @NotNull
  @NonNull
  String claimReference;

  public ClaimType getClaimType() throws InvalidClaimReferenceFormat {
    ClaimType claimType = ClaimType.valueOfLabel(splitClaimReference()[0]);
    if (claimType == null) {
      throw new InvalidClaimReferenceFormat(
          splitClaimReference()[0] + " is not a valid claim type");
    }
    return claimType;
  }

  private String[] splitClaimReference() throws InvalidClaimReferenceFormat {
    String[] claimRefSplit = claimReference.split("(?<=\\D)(?=\\d)");
    if (claimRefSplit.length != 2) {
      throw new InvalidClaimReferenceFormat(
          "Invalid claim reference format. Expected format is for example SW001");
    }

    return claimRefSplit;
  }

}
