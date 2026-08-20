package uk.gov.justice.laa.amend.claim.viewmodels;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

public abstract class ClaimDetailsViewTest<C extends ClaimDetails, V extends ClaimDetailsView<C>> {

  protected abstract C createClaim();

  protected abstract V createView(C claim);

  @Nested
  class GetSubmissionPeriodForDisplayTests {
    @Test
    void getSubmissionPeriodForDisplayHandlesNull() {
      C claim = createClaim();
      V viewModel = createView(claim);
      Assertions.assertNull(viewModel.getSubmissionPeriodForDisplay());
    }

    @Test
    void getSubmissionPeriodForDisplayFormatsDate() {
      C claim = createClaim();
      claim.setSubmissionPeriod(YearMonth.of(2020, 1));
      V viewModel = createView(claim);
      Assertions.assertEquals("Jan 2020", viewModel.getSubmissionPeriodForDisplay());
    }
  }

  @Nested
  class GetSubmissionPeriodForSortingTests {
    @Test
    void getSubmissionPeriodForSortingHandlesNull() {
      C claim = createClaim();
      V viewModel = createView(claim);
      Assertions.assertEquals(0, viewModel.getSubmissionPeriodForSorting());
    }

    @Test
    void getSubmissionPeriodForSortingGetsEpochValueOfDate() {
      C claim = createClaim();
      claim.setSubmissionPeriod(YearMonth.of(2020, 1));
      V viewModel = createView(claim);
      Assertions.assertEquals(18262, viewModel.getSubmissionPeriodForSorting());
    }
  }

  @Nested
  class GetAssessedTotalsTests {
    @Test
    void getAssessedTotalsHandlesNullFields() {
      C claim = createClaim();
      V viewModel = createView(claim);
      List<ClaimFieldRow> result = viewModel.getAssessedTotals();
      Assertions.assertEquals(0, result.size());
    }

    @Test
    void getAssessedTotalsHandlesValidFields() {
      C claim = createClaim();
      claim.setAssessedTotalVat(MockClaimsFunctions.createAssessedTotalVatField());
      claim.setAssessedTotalInclVat(MockClaimsFunctions.createAssessedTotalInclVatField());
      V viewModel = createView(claim);

      List<ClaimFieldRow> result = viewModel.getAssessedTotals();

      Assertions.assertEquals(2, result.size());

      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(0).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/assessed-totals", result.get(0).changeUrl());

      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(1).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/assessed-totals", result.get(1).changeUrl());
    }
  }

  @Nested
  class GetAllowedTotalsTests {
    @Test
    void getAllowedTotalsHandlesNull() {
      C claim = createClaim();
      V viewModel = createView(claim);

      Assertions.assertEquals(List.of(), viewModel.getAllowedTotals());
    }

    @Test
    void getAllowedTotalsHandlesNullCalculatedValues() {
      C claim = createClaim();

      ClaimField allowedTotalVat = MockClaimsFunctions.createAllowedTotalVatField();
      ClaimField allowedTotalInclVat = MockClaimsFunctions.createAllowedTotalInclVatField();

      allowedTotalVat.setCalculated(null);
      allowedTotalInclVat.setCalculated(null);

      claim.setAllowedTotalVat(allowedTotalVat);
      claim.setAllowedTotalInclVat(allowedTotalInclVat);

      V viewModel = createView(claim);

      List<ClaimFieldRow> result = viewModel.getAllowedTotals();

      Assertions.assertEquals(BigDecimal.ZERO, result.get(0).calculated());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/allowed-totals", result.get(0).changeUrl());

      Assertions.assertEquals(BigDecimal.ZERO, result.get(1).calculated());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/allowed-totals", result.get(1).changeUrl());
    }

    @Test
    void getAllowedTotalsHandlesValid() {
      C claim = createClaim();

      ClaimField allowedTotal = MockClaimsFunctions.createAllowedTotalVatField();
      ClaimField allowedTotalInclVat = MockClaimsFunctions.createAllowedTotalInclVatField();

      claim.setAllowedTotalVat(allowedTotal);
      claim.setAllowedTotalInclVat(allowedTotalInclVat);

      V viewModel = createView(claim);

      List<ClaimFieldRow> result = viewModel.getAllowedTotals();

      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(0).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/allowed-totals", result.get(0).changeUrl());

      Assertions.assertEquals(BigDecimal.valueOf(300), result.get(1).assessed());
      Assertions.assertEquals(
          "/submissions/%s/claims/%s/allowed-totals", result.get(1).changeUrl());
    }
  }

  @Nested
  class GetClientNameTests {
    @Test
    void getClientNameHandlesNullForenameAndSurname() {
      C claim = createClaim();
      V viewModel = createView(claim);
      Assertions.assertNull(viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesNullSurname() {
      C claim = createClaim();
      claim.setClientForename("John");
      V viewModel = createView(claim);
      Assertions.assertEquals("John", viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesNullForename() {
      C claim = createClaim();
      claim.setClientSurname("Doe");
      V viewModel = createView(claim);
      Assertions.assertEquals("Doe", viewModel.getClientName());
    }

    @Test
    void getClientNameHandlesFullName() {
      C claim = createClaim();
      claim.setClientForename("John");
      claim.setClientSurname("Doe");
      V viewModel = createView(claim);
      Assertions.assertEquals("John Doe", viewModel.getClientName());
    }
  }
}
