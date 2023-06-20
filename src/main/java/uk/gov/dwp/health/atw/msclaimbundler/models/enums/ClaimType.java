package uk.gov.dwp.health.atw.msclaimbundler.models.enums;

import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.eaForm;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.newOrAmendedDetailsForm;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.swForm;
import static uk.gov.dwp.health.atw.msclaimbundler.config.ClaimFormConfiguration.ttwForm;

import java.util.HashMap;
import java.util.Map;
import uk.gov.dwp.health.atw.msclaimbundler.models.FormConfig;

public enum ClaimType {
  EQUIPMENT_OR_ADAPTATION("EA", eaForm),
  SUPPORT_WORKER("SW", swForm),
  TRAVEL_TO_WORK("TW", ttwForm),
  NEW_OR_AMENDED_DETAILS("NP", newOrAmendedDetailsForm);

  private static final Map<String, ClaimType> BY_LABEL = new HashMap<>();
  public final String label;
  public final FormConfig formConfig;

  ClaimType(String label, FormConfig formConfig) {
    this.label = label;
    this.formConfig = formConfig;
  }

  static {
    for (ClaimType e : values()) {
      BY_LABEL.put(e.label, e);
    }
  }

  public static ClaimType valueOfLabel(String label) {
    return BY_LABEL.get(label);
  }
}