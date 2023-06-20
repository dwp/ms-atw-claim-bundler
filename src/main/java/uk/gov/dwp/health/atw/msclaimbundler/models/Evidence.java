package uk.gov.dwp.health.atw.msclaimbundler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@NoArgsConstructor
@SuperBuilder
@ToString
@AllArgsConstructor
public class Evidence {

  @JsonProperty(value = "fileId")
  @NotNull
  @NonNull
  String fileId;

  @JsonProperty(value = "fileName")
  @NotNull
  @NonNull
  String fileName;
}
