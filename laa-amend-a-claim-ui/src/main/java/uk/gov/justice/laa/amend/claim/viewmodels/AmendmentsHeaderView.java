package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.Optional;
import org.springframework.web.util.HtmlUtils;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.utils.DateUtils;

public record AmendmentsHeaderView(boolean assessedAlertPresent, ThymeleafMessage alertContent) {

  public AmendmentsHeaderView(ClaimDetails claim, MicrosoftApiUser user) {
    this(isAssessed(claim), alertContent(claim, user));
  }

  public static boolean isAssessed(ClaimDetails claim) {
    return claim.isHasAssessment() && claim.getLastAssessment() != null;
  }

  private static ThymeleafMessage alertContent(ClaimDetails claim, MicrosoftApiUser user) {
    String date = DateUtils.displayDateTimeDateValue(claim.getLastUpdatedDateTime());
    String time = DateUtils.displayDateTimeTimeValue(claim.getLastUpdatedDateTime());

    Optional<String> userName =
        Optional.ofNullable(user).map(MicrosoftApiUser::name).map(HtmlUtils::htmlEscape);
    return userName
        .map(name -> new ThymeleafMessage("amendments.assessed.body", name, date, time))
        .orElseGet(() -> new ThymeleafMessage("amendments.assessed.body.noUser", date, time));
  }
}
