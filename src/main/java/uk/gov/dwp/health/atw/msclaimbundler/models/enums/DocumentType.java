package uk.gov.dwp.health.atw.msclaimbundler.models.enums;

public enum DocumentType {
  INVOICE(12075),
  EQUIPMENT_OR_ADAPTATIONS(12067),
  SUPPORT_WORKER(12065),
  TRAVEL_TO_WORK(12068),
  NEW_OR_AMENDED_DETAILS(12070),
  ADAPTATION_TO_VEHICLE(12067);

  public final int id;

  DocumentType(int id) {
    this.id = id;
  }
}
