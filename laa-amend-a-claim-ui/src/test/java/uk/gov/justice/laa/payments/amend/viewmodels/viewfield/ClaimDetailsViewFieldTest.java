package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;

class ClaimDetailsViewFieldTest {

  @Nested
  class ClientNameTests {
    @Test
    void clientNameHandlesNullForenameAndSurname() {
      CivilClaimDetails claim = new CivilClaimDetails();

      Assertions.assertNull(ClaimDetailsViewField.CLIENT_NAME.getGetter().getter().apply(claim));
    }

    @Test
    void clientNameHandlesNullSurname() {
      CivilClaimDetails claim = new CivilClaimDetails();
      claim.setClientForename("John");

      Assertions.assertEquals(
          "John", ClaimDetailsViewField.CLIENT_NAME.getGetter().getter().apply(claim));
    }

    @Test
    void clientNameHandlesNullForename() {
      CivilClaimDetails claim = new CivilClaimDetails();
      claim.setClientSurname("Doe");

      Assertions.assertEquals(
          "Doe", ClaimDetailsViewField.CLIENT_NAME.getGetter().getter().apply(claim));
    }

    @Test
    void clientNameHandlesFullName() {
      CivilClaimDetails claim = new CivilClaimDetails();
      claim.setClientForename("John");
      claim.setClientSurname("Doe");

      Assertions.assertEquals(
          "John Doe", ClaimDetailsViewField.CLIENT_NAME.getGetter().getter().apply(claim));
    }
  }
}
