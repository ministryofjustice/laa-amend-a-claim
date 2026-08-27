package uk.gov.justice.laa.payments.amend.viewmodels;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.service.UserRetrievalService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmendmentsHeaderViewFactory {

  private final UserRetrievalService userRetrievalService;

  public AmendmentsHeaderView create(ClaimDetails claim) {
    MicrosoftApiUser user = null;
    if (AmendmentsHeaderView.isAssessed(claim) && claim.getLastUpdatedUser() != null) {
      user = resolveUser(claim.getLastUpdatedUser());
    }
    return new AmendmentsHeaderView(claim, user);
  }

  private MicrosoftApiUser resolveUser(String userId) {
    try {
      return userRetrievalService.getUser(userId);
    } catch (RuntimeException e) {
      log.warn("Could not resolve last-updated user {} for assessed-claim banner", userId, e);
      return null;
    }
  }
}
