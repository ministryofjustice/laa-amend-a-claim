package uk.gov.justice.laa.amend.claim.viewmodels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.models.AssessmentInfo;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus;

@ExtendWith(MockitoExtension.class)
class AmendmentsHeaderViewFactoryTest {

  @Mock private UserRetrievalService userRetrievalService;

  @InjectMocks private AmendmentsHeaderViewFactory factory;

  private CivilClaimDetails assessedClaim() {
    var claim = new CivilClaimDetails();
    claim.setStatus(ClaimStatus.VALID);
    claim.setHasAssessment(true);
    claim.setLastAssessment(
        AssessmentInfo.builder().lastAssessmentOutcome(OutcomeType.NILLED).build());
    claim.setLastUpdatedUser("user-id");
    claim.setLastUpdatedDateTime(OffsetDateTime.now());
    return claim;
  }

  @Test
  void resolvesUserWhenAssessed() {
    when(userRetrievalService.getUser("user-id"))
        .thenReturn(new MicrosoftApiUser("user-id", "Joe Bloggs", "Joe", "Bloggs"));

    var view = factory.create(assessedClaim());

    assertThat(view.assessedAlertPresent()).isTrue();
    assertThat(view.alertContent().getKey()).isEqualTo("amendments.assessed.body");
    assertThat(view.alertContent().getParams()[0]).isEqualTo("Joe Bloggs");
  }

  @Test
  void stillRendersBannerWhenUserLookupFails() {
    when(userRetrievalService.getUser("user-id")).thenThrow(new RuntimeException("graph down"));

    var view = factory.create(assessedClaim());

    assertThat(view.assessedAlertPresent()).isTrue();
    assertThat(view.alertContent().getKey()).isEqualTo("amendments.assessed.body.noUser");
  }

  @Test
  void doesNotLookUpUserWhenLastAssessmentMissing() {
    var claim = new CivilClaimDetails();
    claim.setStatus(ClaimStatus.VALID);
    claim.setHasAssessment(true);
    claim.setLastAssessment(null);
    claim.setLastUpdatedUser("user-id");

    var view = factory.create(claim);

    assertThat(view.assessedAlertPresent()).isFalse();
    verify(userRetrievalService, never()).getUser("user-id");
  }

  @Test
  void doesNotLookUpUserWhenNoLastUpdatedUser() {
    var claim = assessedClaim();
    claim.setLastUpdatedUser(null);

    var view = factory.create(claim);

    assertThat(view.assessedAlertPresent()).isTrue();
    assertThat(view.alertContent().getKey()).isEqualTo("amendments.assessed.body.noUser");
    verify(userRetrievalService, never()).getUser(null);
  }
}
