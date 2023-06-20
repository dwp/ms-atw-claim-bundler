package uk.gov.dwp.health.atw.msclaimbundler.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClaimToPdfResponse {

  @NonNull
  @JsonProperty("fileId")
  private String fileId;
}
