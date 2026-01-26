package uk.gov.justice.laa.amend.claim.viewmodels;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

public abstract class ClaimDetailsViewTest<C extends ClaimDetails, V extends ClaimDetailsView<C>> {

    protected abstract C createClaim();
    protected abstract V createView(C claim);

    @Nested
    class GetAccountNumberTests {
        @Test
        void getAccountNumberPicksOutFirstPartOfScheduleReference() {
            C claim = createClaim();
            claim.setScheduleReference("0U733A/2018/02");
            V viewModel = createView(claim);
            Assertions.assertEquals("0U733A", viewModel.getAccountNumber());
        }

        @Test
        void getAccountNumberReturnsScheduleReferenceIfUnexpectedFormat() {
            C claim = createClaim();
            claim.setScheduleReference("0U733A201802");
            V viewModel = createView(claim);
            Assertions.assertEquals("0U733A201802", viewModel.getAccountNumber());
        }
    }

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

            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(0).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessed-totals", result.get(0).getChangeUrl());

            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(1).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/assessed-totals", result.get(1).getChangeUrl());
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

            Assertions.assertEquals(BigDecimal.ZERO, result.get(0).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.get(0).getChangeUrl());

            Assertions.assertEquals(BigDecimal.ZERO, result.get(1).getCalculated());
            Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.get(1).getChangeUrl());

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

            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(0).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.get(0).getChangeUrl());

            Assertions.assertEquals(BigDecimal.valueOf(300), result.get(1).getAssessed());
            Assertions.assertEquals("/submissions/%s/claims/%s/allowed-totals", result.get(1).getChangeUrl());
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

    @Nested
    class LastEditedByTests {
        @Test
        void displayLastEditedTextWhenEverythingNonNull() {
            C claim = createClaim();
            AssessmentInfo assessmentInfo = new AssessmentInfo();
            LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 18, 16, 11, 27);
            assessmentInfo.setLastAssessmentDate(OffsetDateTime.of(localDateTime, ZoneOffset.UTC));
            assessmentInfo.setLastAssessmentOutcome(OutcomeType.NILLED);
            claim.setLastAssessment(assessmentInfo);
            V viewModel = createView(claim);
            MicrosoftApiUser user = new MicrosoftApiUser("id", "Joe Bloggs");

            ThymeleafMessage result = viewModel.lastEditedBy(user);

            Assertions.assertEquals("claimSummary.lastAssessmentText", result.getKey());
            Assertions.assertEquals("Joe Bloggs", result.getParams()[0]);
            Assertions.assertEquals("18 December 2025", result.getParams()[1]);
            Assertions.assertEquals("16:11:27", result.getParams()[2]);
            ThymeleafMessage param = (ThymeleafMessage) result.getParams()[3];
            Assertions.assertEquals("outcome.nilled", param.getKey());
            Assertions.assertEquals(0, param.getParams().length);
        }

        @Test
        void displayLastEditedTextWhenUserIsNull() {
            C claim = createClaim();
            AssessmentInfo assessmentInfo = new AssessmentInfo();
            LocalDateTime localDateTime = LocalDateTime.of(2025, 12, 18, 16, 11, 27);
            assessmentInfo.setLastAssessmentDate(OffsetDateTime.of(localDateTime, ZoneOffset.UTC));
            assessmentInfo.setLastAssessmentOutcome(OutcomeType.NILLED);
            claim.setLastAssessment(assessmentInfo);
            V viewModel = createView(claim);

            ThymeleafMessage result = viewModel.lastEditedBy(null);

            Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
            Assertions.assertEquals("18 December 2025", result.getParams()[0]);
            Assertions.assertEquals("16:11:27", result.getParams()[1]);
            ThymeleafMessage param = (ThymeleafMessage) result.getParams()[2];
            Assertions.assertEquals("outcome.nilled", param.getKey());
            Assertions.assertEquals(0, param.getParams().length);
        }
    }
}
