package uk.gov.dwp.health.atw.msclaimbundler.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FormConfig {

  private String atwFormId;
  private Integer drsDocumentId;
}
