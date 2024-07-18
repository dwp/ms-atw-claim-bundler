package uk.gov.dwp.health.atw.msclaimbundler.models.msclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Claimant extends ContactInformation {

  @JsonProperty(value = "company", required = true)
  @NotNull
  @NonNull
  private String company;

}

