package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

public record ClaimDetailsHeaderView(ClaimDetails claim, MicrosoftApiUser user) {

  public boolean isLastAssessedAlertPresent() {
    return claim.isHasAssessment() && claim.getLastAssessment() != null && !claim.isVoided();
  }

  public boolean isVoidedAlertPresent() {
    return claim.isVoided();
  }

  public boolean isHasAssessment() {
    return claim.isHasAssessment();
  }

  public ThymeleafMessage getLastEditedBy() {
    String date = DateUtils.displayDateTimeDateValue(claim().getLastUpdatedDateTime());
    String time = DateUtils.displayDateTimeTimeValue(claim().getLastUpdatedDateTime());

    List<Object> args = new ArrayList<>();
    String editMessageKey = "claimSummary.lastAssessmentText.noUser";
    Optional<String> userName = Optional.ofNullable(user).map(MicrosoftApiUser::name);
    if (userName.isPresent()) {
      args.add(userName.get());
      editMessageKey = "claimSummary.lastAssessmentText";
    }
    args.add(date);
    args.add(time);

    String messageKey =
        (claim.getLastAssessment() != null && !claim.isVoided())
            ? claim.getLastAssessment().lastAssessmentOutcome().getMessageKey()
            : "claimSummary.void.message";
    args.add(new ThymeleafMessage(messageKey));

    return new ThymeleafMessage(editMessageKey, args.toArray());
  }
}
