package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.Claim;

class ClaimViewTest {

  @Nested
  class GetClientNameTests {
    @Test
    void getClientNameHandlesNullForenameAndSurname() {
      Claim claim = new Claim();

      ClaimView viewModel = new ClaimView(claim);

      Assertions.assertNull(viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesNullSurname() {
      Claim claim = new Claim();
      claim.setClientForename("John");

      ClaimView viewModel = new ClaimView(claim);

      Assertions.assertEquals("John", viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesNullForename() {
      Claim claim = new Claim();
      claim.setClientSurname("Doe");

      ClaimView viewModel = new ClaimView(claim);

      Assertions.assertEquals("Doe", viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesFullName() {
      Claim claim = new Claim();
      claim.setClientForename("John");
      claim.setClientSurname("Doe");

      ClaimView viewModel = new ClaimView(claim);

      Assertions.assertEquals("John Doe", viewModel.getClientName());
    }
  }
}
