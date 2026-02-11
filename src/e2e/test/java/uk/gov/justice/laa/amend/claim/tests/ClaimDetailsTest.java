package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.base.E2ETestHelper.loadFixture;
import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;

@Epic("Claim Details")
@Feature("Assessment Outcome Button State")
public class ClaimDetailsTest extends BaseTest {

    // ---------------- Crime data ----------------
    private final String CRIME_PROVIDER_ACCOUNT = "123456";
    private final String CRIME_UFN = "031222/002";
    private final String CRIME_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_ID = UUID.randomUUID().toString();
    private final String CRIME_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CRIME_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    // ---------------- Civil data ----------------
    private final String CIVIL_PROVIDER_ACCOUNT = "234567";
    private final String CIVIL_UFN = "121019/001";
    private final String CIVIL_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_ID = UUID.randomUUID().toString();
    private final String CIVIL_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CIVIL_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    // ---------------- Unescaped data ----------------
    private final String UNESCAPED_UFN = generateUfn();
    private final String UNESCAPED_CLAIM_ID = UUID.randomUUID().toString();
    private final String UNESCAPED_CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String UNESCAPED_CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

    @Override
    protected List<Insert> inserts() {
        return List.of(
                BulkSubmissionInsert.builder()
                        .id(BULK_SUBMISSION_ID)
                        .userId(USER_ID)
                        .build(),
                SubmissionInsert.builder()
                        .id(CRIME_SUBMISSION_ID)
                        .bulkSubmissionId(BULK_SUBMISSION_ID)
                        .officeAccountNumber(CRIME_PROVIDER_ACCOUNT)
                        .submissionPeriod("NOV-2025")
                        .areaOfLaw("CRIME_LOWER")
                        .userId(USER_ID)
                        .build(),
                SubmissionInsert.builder()
                        .id(CIVIL_SUBMISSION_ID)
                        .bulkSubmissionId(BULK_SUBMISSION_ID)
                        .officeAccountNumber(CIVIL_PROVIDER_ACCOUNT)
                        .submissionPeriod("JUN-2025")
                        .areaOfLaw("LEGAL_HELP")
                        .userId(USER_ID)
                        .build(),
                ClaimInsert.builder()
                        .id(CRIME_CLAIM_ID)
                        .submissionId(CRIME_SUBMISSION_ID)
                        .uniqueFileNumber(CRIME_UFN)
                        .userId(USER_ID)
                        .build(),
                ClaimInsert.builder()
                        .id(CIVIL_CLAIM_ID)
                        .submissionId(CIVIL_SUBMISSION_ID)
                        .uniqueFileNumber(CIVIL_UFN)
                        .userId(USER_ID)
                        .build(),
                ClaimInsert.builder()
                        .id(UNESCAPED_CLAIM_ID)
                        .submissionId(CRIME_SUBMISSION_ID)
                        .uniqueFileNumber(UNESCAPED_UFN)
                        .userId(USER_ID)
                        .build(),
                ClaimSummaryFeeInsert.builder()
                        .id(CRIME_CLAIM_SUMMARY_FEE_ID)
                        .claimId(CRIME_CLAIM_ID)
                        .userId(USER_ID)
                        .build(),
                ClaimSummaryFeeInsert.builder()
                        .id(CIVIL_CLAIM_SUMMARY_FEE_ID)
                        .claimId(CIVIL_CLAIM_ID)
                        .userId(USER_ID)
                        .build(),
                ClaimSummaryFeeInsert.builder()
                        .id(UNESCAPED_CLAIM_SUMMARY_FEE_ID)
                        .claimId(UNESCAPED_CLAIM_ID)
                        .userId(USER_ID)
                        .build(),
                CalculatedFeeDetailInsert.builder()
                        .id(CRIME_CALCULATED_FEE_DETAIL_ID)
                        .claimSummaryFeeId(CRIME_CLAIM_SUMMARY_FEE_ID)
                        .claimId(CRIME_CLAIM_ID)
                        .escaped(true)
                        .userId(USER_ID)
                        .build(),
                CalculatedFeeDetailInsert.builder()
                        .id(CIVIL_CALCULATED_FEE_DETAIL_ID)
                        .claimSummaryFeeId(CIVIL_CLAIM_SUMMARY_FEE_ID)
                        .claimId(CIVIL_CLAIM_ID)
                        .escaped(true)
                        .userId(USER_ID)
                        .build(),
                CalculatedFeeDetailInsert.builder()
                        .id(UNESCAPED_CALCULATED_FEE_DETAIL_ID)
                        .claimSummaryFeeId(UNESCAPED_CLAIM_SUMMARY_FEE_ID)
                        .claimId(UNESCAPED_CLAIM_ID)
                        .escaped(false)
                        .userId(USER_ID)
                        .build());
    }

    static Stream<ClaimDetailsFixture> detailsCases() {
        return Stream.of(
                // crime
                loadFixture("fixtures/claim-details/crime-111018-001.json"),
                // civil
                loadFixture("fixtures/claim-details/civil-121019-001.json"));
    }

    @Test
    @Story("Non-escape claim")
    @DisplayName("Claim Details: Add assessment outcome is disabled for non-escape claims")
    @Severity(SeverityLevel.CRITICAL)
    void addAssessmentOutcomeIsDisabledForNonEscapeClaim() {
        SearchPage search = new SearchPage(page);

        search.searchForClaim(CRIME_PROVIDER_ACCOUNT, "11", "2025", UNESCAPED_UFN, "");

        search.clickViewForUfn(UNESCAPED_UFN);

        ClaimDetailsPage details = new ClaimDetailsPage(page);

        Assertions.assertTrue(
                details.isAddAssessmentOutcomeDisabled(),
                "Expected Add assessment outcome button to be disabled for non-escape claim");
    }

    /**
     * Verifying Totals has been removed from test data, until we fix test data
     *
     * @param claimDetailsFixture
     */
    @ParameterizedTest(name = "[{index}] {0} Claim: Values match fixture")
    @MethodSource("detailsCases")
    @DisplayName("Claim Details: Values match fixture")
    @Severity(SeverityLevel.CRITICAL)
    void claimValuesMatchFixture(ClaimDetailsFixture claimDetailsFixture) {
        SearchPage search = new SearchPage(page);

        search.searchForClaim(claimDetailsFixture.getProviderAccount(), "", "", claimDetailsFixture.getUfn(), "");

        search.clickViewForUfn(claimDetailsFixture.getUfn());

        ClaimDetailsPage details = new ClaimDetailsPage(page);

        Assertions.assertFalse(
                details.isAddAssessmentOutcomeDisabled(),
                "Expected Add assessment outcome button to be enabled for escape claim");

        Assertions.assertEquals(
                claimDetailsFixture.isAddAssessmentOutcomeDisabled(),
                details.isAddAssessmentOutcomeDisabled(),
                "Add assessment outcome enabled/disabled state mismatch");

        details.assertAllValues(claimDetailsFixture.getValues());
    }
}
