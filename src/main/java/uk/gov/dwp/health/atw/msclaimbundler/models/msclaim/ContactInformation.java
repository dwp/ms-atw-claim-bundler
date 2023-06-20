package uk.gov.dwp.health.atw.msclaimbundler.models.msclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
@NoArgsConstructor
public class ContactInformation {

  @JsonProperty(value = "forename")
  String forename;

  @JsonProperty(value = "surname")
  String surname;

  @JsonProperty(value = "dateOfBirth")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  LocalDate dateOfBirth;

  @JsonProperty(value = "emailAddress")
  String emailAddress;

  @JsonProperty(value = "homeNumber")
  String homeNumber;

  @JsonProperty(value = "mobileNumber")
  String mobileNumber;

  @JsonProperty(value = "address")
  Address address;
}
