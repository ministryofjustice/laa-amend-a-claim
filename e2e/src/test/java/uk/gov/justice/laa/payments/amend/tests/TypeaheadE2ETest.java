package uk.gov.justice.laa.payments.amend.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.laa.payments.amend.utils.TestDataUtils.generateUfn;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.base.BaseTest;
import uk.gov.justice.laa.payments.amend.models.BulkSubmissionInsert;
import uk.gov.justice.laa.payments.amend.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimCaseInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimInsert;
import uk.gov.justice.laa.payments.amend.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.payments.amend.models.ClientInsert;
import uk.gov.justice.laa.payments.amend.models.Insert;
import uk.gov.justice.laa.payments.amend.models.SubmissionInsert;
import uk.gov.justice.laa.payments.amend.pages.ClaimDetailsPage;
import uk.gov.justice.laa.payments.amend.pages.SearchPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendFeeCodePage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendmentRequestedByPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.AmendmentRequestedReasonPage;
import uk.gov.justice.laa.payments.amend.pages.amendments.ViewCasePage;
import uk.gov.justice.laa.payments.amend.pages.amendments.ViewClientPage;

class TypeaheadE2ETest extends BaseTest {

  private static final String PROVIDER_ACCOUNT = "0P322F";
  private static final String UFN = generateUfn();
  private static final String SUBMISSION_ID = UUID.randomUUID().toString();
  private static final String CLAIM_ID = UUID.randomUUID().toString();
  private static final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
  private static final String EXISTING_FEE_CODE = "IMCA";
  private static final String QUERY = "IAX";
  private static final String MATCHING_FEE_CODE = "IAXC";

  private AmendFeeCodePage feeCodePage;

  @Override
  protected List<Insert> inserts() {
    return List.of(
        BulkSubmissionInsert.builder().id(BULK_SUBMISSION_ID).userId(USER_ID).build(),
        SubmissionInsert.builder()
            .id(SUBMISSION_ID)
            .bulkSubmissionId(BULK_SUBMISSION_ID)
            .officeAccountNumber(PROVIDER_ACCOUNT)
            .submissionPeriod("MAR-2020")
            .areaOfLaw("LEGAL_HELP")
            .userId(USER_ID)
            .build(),
        ClaimInsert.builder()
            .id(CLAIM_ID)
            .submissionId(SUBMISSION_ID)
            .uniqueFileNumber(UFN)
            .userId(USER_ID)
            .build(),
        ClientInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(CLAIM_ID)
            .clientForename("Francesca")
            .clientSurname("Elonga")
            .clientDateOfBirth("1996-08-07")
            .uniqueClientNumber("07081996/S/MCLE")
            .clientPostcode("SE61LG")
            .genderCode("F")
            .ethnicityCode("03")
            .disabilityCode("NCD")
            .userId(USER_ID)
            .build(),
        ClaimCaseInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimId(CLAIM_ID)
            .caseId("711")
            .uniqueCaseId("UCID123456")
            .outcomeCode("BB")
            .stageReachedCode("--")
            .userId(USER_ID)
            .build(),
        ClaimSummaryFeeInsert.builder()
            .id(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .userId(USER_ID)
            .build(),
        CalculatedFeeDetailInsert.builder()
            .id(UUID.randomUUID().toString())
            .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
            .claimId(CLAIM_ID)
            .feeCode(EXISTING_FEE_CODE)
            .escaped(true)
            .userId(USER_ID)
            .build());
  }

  @BeforeEach
  void openFeeCodeTypeahead() {
    var searchPage = new SearchPage(page);
    searchPage.searchForClaim(PROVIDER_ACCOUNT, "03", "2020", UFN, "", "", "");
    searchPage.clickViewForUfn(UFN);

    new ClaimDetailsPage(page).clickAmendClaim();

    var requestedByPage = new AmendmentRequestedByPage(page);
    requestedByPage.getProviderRadio().click();
    requestedByPage.getContinueButton().click();

    var requestedReasonPage = new AmendmentRequestedReasonPage(page);
    requestedReasonPage.getProviderErrorRadio().click();
    requestedReasonPage.getContinueButton().click();

    new ViewClientPage(page).clickCaseTab();
    new ViewCasePage(page).clickChangeCaseTypeLink();

    feeCodePage = new AmendFeeCodePage(page);
  }

  @Test
  @DisplayName("Fee code typeahead opens with matching options only after typing")
  void opensWithMatchingOptionsOnlyAfterTyping() {
    assertThat(feeCodePage.getFeeCodeInput()).hasValue(EXISTING_FEE_CODE);
    assertThat(feeCodePage.getFeeCodeMenu()).isHidden();

    feeCodePage.getFeeCodeInput().click();

    assertThat(feeCodePage.getFeeCodeMenu()).isHidden();
    assertTrue(feeCodePage.isAllInputTextSelected());
    feeCodePage.getFeeCodeInput().pressSequentially(QUERY);

    assertThat(feeCodePage.getFeeCodeInput()).hasValue(QUERY);
    assertThat(feeCodePage.getFeeCodeMenu()).isVisible();
    assertThat(feeCodePage.getFeeCodeMenu()).containsText(MATCHING_FEE_CODE);
    assertTrue(
        feeCodePage
            .getFeeCodeMenu()
            .locator(".autocomplete__option")
            .allTextContents()
            .stream()
            .allMatch(
                option ->
                    option.toLowerCase(Locale.ROOT).contains(QUERY.toLowerCase(Locale.ROOT))));
  }
}
