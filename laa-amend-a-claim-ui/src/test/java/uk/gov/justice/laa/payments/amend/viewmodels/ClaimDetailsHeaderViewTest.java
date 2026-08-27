package uk.gov.justice.laa.payments.amend.viewmodels;

import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.AMENDED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.ASSESSED;
import static uk.gov.justice.laa.payments.amend.models.enums.DerivedClaimStatus.VOIDED;
import static uk.gov.justice.laa.payments.amend.models.enums.OutcomeType.NILLED;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.payments.amend.models.AssessmentInfo;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;

public class ClaimDetailsHeaderViewTest {

  // UTC 14:30:00 on a BST day (June) = London 3:30pm
  private static final OffsetDateTime LAST_ASSESSMENT_DATE =
      OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC);

  @Nested
  class LastEditedByTests {
    @Test
    void displayLastEditedTextWhenUserValuesAreNonNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo = AssessmentInfo.builder().lastAssessmentOutcome(NILLED).build();

      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
      claim.setDerivedClaimStatus(ASSESSED);

      MicrosoftApiUser user = new MicrosoftApiUser("id", "Bloggs, Joe", "Joe", "Bloggs");
      var viewModel = new ClaimDetailsHeaderView(claim, user, LAST_ASSESSMENT_DATE);

      ThymeleafMessage result = viewModel.lastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText", result.getKey());
      Assertions.assertEquals("Joe Bloggs", result.getParams()[0]);
      Assertions.assertEquals("15 June 2025", result.getParams()[1]);
      Assertions.assertEquals("3:30pm", result.getParams()[2]);

      ThymeleafMessage extraAlertText = viewModel.extraAlertText();
      Assertions.assertEquals("outcome.nilled", extraAlertText.getKey());
      Assertions.assertEquals(0, extraAlertText.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenUserValuesAreNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo = AssessmentInfo.builder().lastAssessmentOutcome(NILLED).build();
      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
      claim.setDerivedClaimStatus(ASSESSED);

      MicrosoftApiUser user = new MicrosoftApiUser("id", null, null, null);
      var viewModel = new ClaimDetailsHeaderView(claim, user, LAST_ASSESSMENT_DATE);

      ThymeleafMessage result = viewModel.lastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);

      ThymeleafMessage extraAlertText = viewModel.extraAlertText();
      Assertions.assertEquals("outcome.nilled", extraAlertText.getKey());
      Assertions.assertEquals(0, extraAlertText.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenUserIsNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo = AssessmentInfo.builder().lastAssessmentOutcome(NILLED).build();
      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
      claim.setStatus(VALID);
      claim.setDerivedClaimStatus(ASSESSED);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      ThymeleafMessage result = viewModel.lastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);

      ThymeleafMessage extraAlertText = viewModel.extraAlertText();
      Assertions.assertEquals("outcome.nilled", extraAlertText.getKey());
      Assertions.assertEquals(0, extraAlertText.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenClaimVoided() {
      var claim = new CivilClaimDetails();
      claim.setStatus(VOID);
      claim.setDerivedClaimStatus(VOIDED);

      MicrosoftApiUser user = new MicrosoftApiUser("id", null, null, null);

      var viewModel = new ClaimDetailsHeaderView(claim, user, LAST_ASSESSMENT_DATE);

      ThymeleafMessage result = viewModel.lastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);

      ThymeleafMessage extraAlertText = viewModel.extraAlertText();
      Assertions.assertEquals("claimSummary.void.message", extraAlertText.getKey());
    }

    @Test
    void displayNoExtraAlertTextWhenDerivedClaimStatusIsNull() {
      var claim = new CivilClaimDetails();
      claim.setStatus(VALID);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertNull(viewModel.extraAlertText());
    }
  }

  @Nested
  class StatusTests {
    @Test
    void displayNoExtraAlertTextWhenDerivedClaimStatusIsNull() {
      var claim = new CivilClaimDetails();
      claim.setStatus(VALID);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertNull(viewModel.extraAlertText());
    }

    @Test
    void showOnlyAmendedAlertWhenClaimIsAmended() {
      var claim = new CivilClaimDetails();
      claim.setAmended(true);
      claim.setDerivedClaimStatus(AMENDED);
      claim.setHasAssessment(false);
      claim.setStatus(VALID);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertTrue(viewModel.isAmendedAlertPresent());
      Assertions.assertFalse(viewModel.isLastAssessedAlertPresent());
      Assertions.assertFalse(viewModel.isVoidedAlertPresent());
    }

    @Test
    void showOnlyAssessedAlertWhenClaimIsAssessed() {
      var claim = new CivilClaimDetails();
      claim.setAmended(true);
      claim.setHasAssessment(true);
      claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(NILLED).build());
      claim.setStatus(VALID);
      claim.setDerivedClaimStatus(ASSESSED);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertFalse(viewModel.isAmendedAlertPresent());
      Assertions.assertTrue(viewModel.isLastAssessedAlertPresent());
      Assertions.assertFalse(viewModel.isVoidedAlertPresent());
    }

    @Test
    void showNoAlertsWhenAssessedClaimHasNoLastAssessment() {
      var claim = new CivilClaimDetails();
      claim.setHasAssessment(true);
      claim.setStatus(VALID);
      claim.setDerivedClaimStatus(ASSESSED);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertFalse(viewModel.isAmendedAlertPresent());
      Assertions.assertFalse(viewModel.isLastAssessedAlertPresent());
      Assertions.assertFalse(viewModel.isVoidedAlertPresent());
    }

    @Test
    void showOnlyVoidedAlertWhenClaimIsVoided() {
      var claim = new CivilClaimDetails();
      claim.setAmended(true);
      claim.setHasAssessment(true);
      claim.setLastAssessment(AssessmentInfo.builder().lastAssessmentOutcome(NILLED).build());
      claim.setStatus(VOID);
      claim.setDerivedClaimStatus(VOIDED);

      var viewModel = new ClaimDetailsHeaderView(claim, null, LAST_ASSESSMENT_DATE);

      Assertions.assertFalse(viewModel.isAmendedAlertPresent());
      Assertions.assertFalse(viewModel.isLastAssessedAlertPresent());
      Assertions.assertTrue(viewModel.isVoidedAlertPresent());
    }
  }
}
