package uk.gov.justice.laa.amend.claim.tests;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;
import uk.gov.justice.laa.amend.claim.pages.ClaimDetailsPage;
import uk.gov.justice.laa.amend.claim.pages.SearchPage;
import uk.gov.justice.laa.amend.claim.pages.VoidConfirmationPage;

public class VoidClaimFlowE2ETest extends BaseTest {

  private static final String PROVIDER_ACCOUNT = "123456";
  private static final String UFN = generateUfn();
  private static final String SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CLAIM_ID = UUID.randomUUID().toString();
  private static final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();

  @Override
  protected List<Insert> inserts() {
    return List.of(
        BulkSubmissionInsert.builder().id(BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(SUBMISSION_ID)
            .bulkSubmissionId(BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("CRIME_LOWER")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(CLAIM_ID)
            .submissionId(SUBMISSION_ID)
            .uniqueFileNumber(UFN)
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(CALCULATED_FEE_DETAIL_ID)
            .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .feeCode("INVC")
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @Test
  @DisplayName("E2E: Void Claim Flow – Search → View → Void Confirmation → Search")
  void fullVoidFlow() {
    var search = new SearchPage(page);

    search.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", UFN, "", "", "");

    search.clickViewForUfn(UFN);

    var details = new ClaimDetailsPage(page);
    details.clickVoidClaim();

    var voidConfirmation = new VoidConfirmationPage(page);
    voidConfirmation.clickVoidClaimButton();

    var searchAfterVoid = new SearchPage(page);
    Assertions.assertEquals("You voided the claim", searchAfterVoid.getSuccessBannerHeading());
  }
}
