package uk.gov.justice.laa.amend.claim.viewmodels;

import static uk.gov.justice.laa.amend.claim.models.enums.DerivedClaimStatus.ASSESSED;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

public record ClaimDetailsHeaderView(
    boolean isAmendedAlertPresent,
    boolean isLastAssessedAlertPresent,
    boolean isVoidedAlertPresent,
    ThymeleafMessage lastEditedBy,
    ThymeleafMessage extraAlertText) {

  public ClaimDetailsHeaderView(
      ClaimDetails claim, MicrosoftApiUser lastUpdatedUser, OffsetDateTime lastUpdatedDateTime) {
    this(
        isAmendedAlertPresent(claim),
        isLastAssessedAlertPresent(claim),
        isVoidedAlertPresent(claim),
        lastEditedBy(lastUpdatedUser, lastUpdatedDateTime),
        extraAlertText(claim));
  }

  private static boolean isAmendedAlertPresent(ClaimDetails claim) {
    return claim.isAmended() && !claim.isHasAssessment() && !claim.isVoided();
  }

  private static boolean isLastAssessedAlertPresent(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null && !claim.isVoided();
  }

  private static boolean isVoidedAlertPresent(ClaimDetails claim) {
    return claim.isVoided();
  }

  private static ThymeleafMessage lastEditedBy(
      MicrosoftApiUser lastUpdatedUser, OffsetDateTime lastUpdatedDateTime) {
    String date = DateUtils.displayDateTimeDateValue(lastUpdatedDateTime);
    String time = DateUtils.displayDateTimeTimeValue(lastUpdatedDateTime);

    var args = new ArrayList<>();
    var editMessageKey = "claimSummary.lastAssessmentText.noUser";
    var userName = Optional.ofNullable(lastUpdatedUser).map(MicrosoftApiUser::name);
    if (userName.isPresent()) {
      args.add(userName.get());
      editMessageKey = "claimSummary.lastAssessmentText";
    }
    args.add(date);
    args.add(time);

    return new ThymeleafMessage(editMessageKey, args.toArray());
  }

  private static ThymeleafMessage extraAlertText(ClaimDetails claim) {
    if (claim.getDerivedClaimStatus() == null) {
      return null;
    }

    return switch (claim.getDerivedClaimStatus()) {
      case ASSESSED -> {
        if (claim.getLastAssessment() != null) {
          yield new ThymeleafMessage(
              claim.getLastAssessment().lastAssessmentOutcome().getMessageKey());
        } else {
          yield null;
        }
      }
      case VOIDED -> new ThymeleafMessage("claimSummary.void.message");
      default -> null;
    };
  }
}
