package uk.gov.dwp.health.atw.msclaimbundler.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class ContactRetrievalRequest {

  @JsonProperty(value = "requestId")
  @NotNull
  @NonNull
  String id;
}
