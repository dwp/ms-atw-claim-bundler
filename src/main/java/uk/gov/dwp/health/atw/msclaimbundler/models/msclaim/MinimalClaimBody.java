package uk.gov.dwp.health.atw.msclaimbundler.models.msclaim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import uk.gov.dwp.health.atw.msclaimbundler.models.Evidence;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimStatus;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.ClaimType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimalClaimBody {

  @JsonProperty(value = "nino", required = true)
  @NotNull
  @NonNull
  @Size(min = 8, max = 9)
  private String nino;

  @JsonProperty(value = "atwNumber", required = true)
  @NotNull
  @NonNull
  private String atwNumber;

  @JsonProperty(value = "claimType", required = true)
  @NotNull
  @NonNull
  private ClaimType claimType;

  @JsonProperty(value = "claimant", required = true)
  @NotNull
  @NonNull
  private Claimant claimant;

  @JsonProperty(value = "createdDate", required = true)
  @NonNull
  @NotNull
  private LocalDateTime createdDate;

  @JsonProperty(value = "claimStatus", required = true)
  @NonNull
  @NotNull
  private ClaimStatus claimStatus;

  @JsonProperty(value = "evidence")
  private List<Evidence> evidence;

  @JsonProperty(value = "payee", required = true)
  @NotNull
  @NonNull
  private Payee payee;

  @JsonProperty(value = "declarationVersion", required = true)
  @NonNull
  @NotNull
  private double declarationVersion;
}
