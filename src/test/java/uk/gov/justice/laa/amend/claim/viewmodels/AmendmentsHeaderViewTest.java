package uk.gov.justice.laa.amend.claim.viewmodels;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

class AmendmentsHeaderViewTest {

  private CivilClaimDetails assessedClaim() {
    var claim = new CivilClaimDetails();
    var assessmentInfo =
        AssessmentInfo.builder()
            // UTC 14:30:00 on a BST day (June) = London 3:30pm
            .lastAssessmentDate(
                OffsetDateTime.of(LocalDateTime.of(2025, 6, 15, 14, 30, 0), ZoneOffset.UTC))
            .lastAssessmentOutcome(OutcomeType.NILLED)
            .build();
    claim.setHasAssessment(true);
    claim.setLastAssessment(assessmentInfo);
    claim.setLastUpdatedUser(assessmentInfo.lastAssessedBy());
    claim.setLastUpdatedDateTime(assessmentInfo.lastAssessmentDate());
    claim.setStatus(ClaimStatus.VALID);
    return claim;
  }

  @Test
  void showsAssessedAlertWithUserDateAndTime() {
    var user = new MicrosoftApiUser("id", "Bloggs, Joe", "Joe", "Bloggs");

    var viewModel = new AmendmentsHeaderView(assessedClaim(), user);

    Assertions.assertTrue(viewModel.assessedAlertPresent());
    ThymeleafMessage content = viewModel.alertContent();
    Assertions.assertEquals("amendments.assessed.body", content.getKey());
    Assertions.assertEquals("Joe Bloggs", content.getParams()[0]);
    Assertions.assertEquals("15 June 2025", content.getParams()[1]);
    Assertions.assertEquals("3:30pm", content.getParams()[2]);
  }

  @Test
  void escapesHtmlInUserName() {
    var user = new MicrosoftApiUser("id", "<script>alert(1)</script>", null, null);

    var viewModel = new AmendmentsHeaderView(assessedClaim(), user);

    ThymeleafMessage content = viewModel.alertContent();
    Assertions.assertEquals("&lt;script&gt;alert(1)&lt;/script&gt;", content.getParams()[0]);
  }

  @Test
  void usesNoUserMessageWhenUserIsNull() {
    var viewModel = new AmendmentsHeaderView(assessedClaim(), null);

    ThymeleafMessage content = viewModel.alertContent();
    Assertions.assertEquals("amendments.assessed.body.noUser", content.getKey());
    Assertions.assertEquals("15 June 2025", content.getParams()[0]);
    Assertions.assertEquals("3:30pm", content.getParams()[1]);
  }

  @Test
  void doesNotShowAlertWhenClaimNotAssessed() {
    var claim = new CivilClaimDetails();
    claim.setHasAssessment(false);
    claim.setStatus(ClaimStatus.VALID);

    var viewModel = new AmendmentsHeaderView(claim, null);

    Assertions.assertFalse(viewModel.assessedAlertPresent());
  }
}
