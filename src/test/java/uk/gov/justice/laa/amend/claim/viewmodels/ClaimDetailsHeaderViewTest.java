package uk.gov.justice.laa.amend.claim.viewmodels;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

public class ClaimDetailsHeaderViewTest {

  @Nested
  class LastEditedByTests {
    @Test
    void displayLastEditedTextWhenUserValuesAreNonNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo =
          AssessmentInfo.builder()
              // UTC 14:30:00 on a BST day (June) = London 3:30pm
              .lastAssessmentDate(
                  OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC))
              .lastAssessmentOutcome(OutcomeType.NILLED)
              .build();

      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());

      MicrosoftApiUser user = new MicrosoftApiUser("id", "Bloggs, Joe", "Joe", "Bloggs");
      var viewModel = new ClaimDetailsHeaderView(claim, user);

      ThymeleafMessage result = viewModel.getLastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText", result.getKey());
      Assertions.assertEquals("Joe Bloggs", result.getParams()[0]);
      Assertions.assertEquals("15 June 2025", result.getParams()[1]);
      Assertions.assertEquals("3:30pm", result.getParams()[2]);
      ThymeleafMessage param = (ThymeleafMessage) result.getParams()[3];
      Assertions.assertEquals("outcome.nilled", param.getKey());
      Assertions.assertEquals(0, param.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenUserValuesAreNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo =
          AssessmentInfo.builder()
              // UTC 14:30:00 on a BST day (June) = London 3:30pm
              .lastAssessmentDate(
                  OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC))
              .lastAssessmentOutcome(OutcomeType.NILLED)
              .build();
      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());

      MicrosoftApiUser user = new MicrosoftApiUser("id", null, null, null);
      var viewModel = new ClaimDetailsHeaderView(claim, user);

      ThymeleafMessage result = viewModel.getLastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);
      ThymeleafMessage param = (ThymeleafMessage) result.getParams()[2];
      Assertions.assertEquals("outcome.nilled", param.getKey());
      Assertions.assertEquals(0, param.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenUserIsNull() {
      var claim = new CivilClaimDetails();
      var assessmentInfo =
          AssessmentInfo.builder()
              // UTC 14:30:00 on a BST day (June) = London 3:30pm
              .lastAssessmentDate(
                  OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC))
              .lastAssessmentOutcome(OutcomeType.NILLED)
              .build();
      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
      claim.setStatus(ClaimStatus.VALID);

      var viewModel = new ClaimDetailsHeaderView(claim, null);

      ThymeleafMessage result = viewModel.getLastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);
      ThymeleafMessage param = (ThymeleafMessage) result.getParams()[2];
      Assertions.assertEquals("outcome.nilled", param.getKey());
      Assertions.assertEquals(0, param.getParams().length);
    }

    @Test
    void displayLastEditedTextWhenClaimVoided() {
      var claim = new CivilClaimDetails();
      var assessmentInfo =
          AssessmentInfo.builder()
              // UTC 14:30:00 on a BST day (June) = London 3:30pm
              .lastAssessmentDate(
                  OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC))
              .lastAssessmentOutcome(OutcomeType.NILLED)
              .build();
      claim.setLastAssessment(assessmentInfo);
      claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
      claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
      claim.setStatus(ClaimStatus.VOID);

      MicrosoftApiUser user = new MicrosoftApiUser("id", null, null, null);

      var viewModel = new ClaimDetailsHeaderView(claim, user);

      ThymeleafMessage result = viewModel.getLastEditedBy();

      Assertions.assertEquals("claimSummary.lastAssessmentText.noUser", result.getKey());
      Assertions.assertEquals("15 June 2025", result.getParams()[0]);
      Assertions.assertEquals("3:30pm", result.getParams()[1]);
      ThymeleafMessage param = (ThymeleafMessage) result.getParams()[2];
      Assertions.assertEquals("claimSummary.void.message", param.getKey());
    }
  }
}
