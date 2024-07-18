package uk.gov.dwp.health.atw.msclaimbundler.models.msclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;


@Data
@NoArgsConstructor
@Jacksonized
@SuperBuilder
public class NewPayeeDetails {

  @JsonProperty(value = "accessToWorkNumber", required = true)
  @NotNull
  @NonNull
  private String accessToWorkNumber;

  @JsonProperty(value = "declarationVersion", required = true)
  @NotNull
  @NonNull
  private double declarationVersion;

  @JsonProperty(value = "currentContactInformation", required = true)
  @NotNull
  @NonNull
  private ContactInformation currentContactInformation;

  @JsonProperty(value = "createdDate", required = true)
  @NonNull
  @NotNull
  private LocalDateTime createdDate;

  @JsonProperty(value = "payee", required = true)
  @NotNull
  @NonNull
  private Payee payee;

}
