package uk.gov.dwp.health.atw.msclaimbundler.config;

import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.EQUIPMENT_OR_ADAPTATIONS;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.NEW_OR_AMENDED_DETAILS;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.SUPPORT_WORKER;
import static uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType.TRAVEL_TO_WORK;

import uk.gov.dwp.health.atw.msclaimbundler.models.FormConfig;


public final class ClaimFormConfiguration {
  ClaimFormConfiguration() {
    throw new IllegalStateException("Utility class");
  }

  public static final FormConfig eaForm =
      new FormConfig("DP224JP", EQUIPMENT_OR_ADAPTATIONS.id);
  public static final FormConfig swForm = new FormConfig("DP222JP", SUPPORT_WORKER.id);
  public static final FormConfig ttwForm = new FormConfig("DP226JP", TRAVEL_TO_WORK.id);
  public static final FormConfig newOrAmendedDetailsForm =
      new FormConfig("DP228JP", NEW_OR_AMENDED_DETAILS.id);

}
