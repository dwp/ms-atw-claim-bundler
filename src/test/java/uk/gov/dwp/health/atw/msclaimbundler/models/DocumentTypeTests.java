package uk.gov.dwp.health.atw.msclaimbundler.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.health.atw.msclaimbundler.models.enums.DocumentType;

class DocumentTypeTests {

  @Test
  @DisplayName("Test EA Document Type")
  void testEaDocumentType() {
    assertEquals(12067, DocumentType.EQUIPMENT_OR_ADAPTATIONS.id);
  }

  @Test
  @DisplayName("Test SW Document Type")
  void testSwDocumentType() {
    assertEquals(12065, DocumentType.SUPPORT_WORKER.id);
  }

  @Test
  @DisplayName("Test TTW Document Type")
  void testTtwDocumentType() {
    assertEquals(12068, DocumentType.TRAVEL_TO_WORK.id);
  }

  @Test
  @DisplayName("Test TIW Document Type")
  void testTiwDocumentType() {
    assertEquals(12069, DocumentType.TRAVEL_IN_WORK.id);
  }

  @Test
  @DisplayName("Test Invoice Document Type")
  void testInvoiceDocumentType() {
    assertEquals(12075, DocumentType.INVOICE.id);
  }

  @Test
  @DisplayName("Test AV Document Type")
  void testAvDocumentType() {
    assertEquals(12067, DocumentType.ADAPTATION_TO_VEHICLE.id);
  }
}
