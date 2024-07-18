package uk.gov.dwp.health.atw.msclaimbundler.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import nonapi.io.github.classgraph.json.Id;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ContactInformationStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.msclaim.ContactInformation;

@Data
@NoArgsConstructor
@Jacksonized
@SuperBuilder
public class ContactInformationRequest {

  @Id
  String id;

  @JsonProperty(value = "accessToWorkNumber")
  @NotNull
  @NonNull
  String accessToWorkNumber;

  @JsonProperty(value = "nino")
  @NotNull
  @NonNull
  String nino;

  @JsonProperty(value = "declarationVersion", required = true)
  @NotNull
  @NonNull
  private Double declarationVersion;

  @JsonProperty(value = "currentContactInformation", required = true)
  @NotNull
  @NonNull
  private ContactInformation currentContactInformation;

  @JsonProperty(value = "newContactInformation", required = true)
  @NotNull
  @NonNull
  private ContactInformation newContactInformation;

  @JsonProperty(value = "createdDate", required = true)
  @NotNull
  @NonNull
  private LocalDateTime createdDate;

  @JsonProperty(value = "contactInformationStatus", required = true)
  @NotNull
  @NonNull
  private ContactInformationStatus contactInformationStatus;
}
