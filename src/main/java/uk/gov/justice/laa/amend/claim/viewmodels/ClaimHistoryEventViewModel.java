package uk.gov.justice.laa.amend.claim.viewmodels;

import java.time.OffsetDateTime;
import java.util.List;
import uk.gov.justice.laa.amend.claim.models.ClaimHistoryEvent;

public record ClaimHistoryEventViewModel(
    ThymeleafMessage type,
    OffsetDateTime eventDateTime,
    ThymeleafString user,
    List<ThymeleafMessage> descriptions) {

  private static final String BY_USER_KEY = "claimHistory.byUser";
  private static final ThymeleafMessage USER_NOT_AVAILABLE =
      new ThymeleafMessage("claimHistory.userNotAvailable");

  private static final ThymeleafMessage CLAIM_CREATED_DESCRIPTION =
      new ThymeleafMessage("claimHistory.claimCreated.description");
  private static final ThymeleafMessage CLAIM_ESCAPED_DESCRIPTION =
      new ThymeleafMessage("claimHistory.claimEscaped.description");
  private static final ThymeleafMessage CLAIM_CALCULATED_DESCRIPTION =
      new ThymeleafMessage("claimHistory.claimCalculated.description");
  private static final ThymeleafMessage CLAIM_VOIDED_DESCRIPTION =
      new ThymeleafMessage("claimHistory.claimVoided.description");

  private static final String CLAIM_ASSESSED_ESCAPE_CASE_DESCRIPTION_KEY =
      "claimHistory.claimAssessedEscapeCase.description";
  private static final String CLAIM_ASSESSED_STAGE_DISBURSEMENT_DESCRIPTION_KEY =
      "claimHistory.claimAssessedStageDisbursement.description";

  public static ClaimHistoryEventViewModel create(ClaimHistoryEvent event) {
    return new ClaimHistoryEventViewModel(
        toTypeMessage(event), event.eventDateTime(), toUserMessage(event), toDescriptions(event));
  }

  private static ThymeleafMessage toTypeMessage(ClaimHistoryEvent event) {
    var messageKey =
        switch (event.type()) {
          case CLAIM_CREATED, CLAIM_CREATED_AND_ESCAPED -> "claimHistory.claimCreated.type";
          case CLAIM_ASSESSED_ESCAPE_CASE -> "claimHistory.claimAssessedEscapeCase.type";
          case CLAIM_ASSESSED_STAGE_DISBURSEMENT ->
              "claimHistory.claimAssessedStageDisbursement.type";
          case CLAIM_VOIDED -> "claimHistory.claimVoided.type";
        };
    return new ThymeleafMessage(messageKey);
  }

  private static ThymeleafString toUserMessage(ClaimHistoryEvent event) {
    if (event.user() == null) {
      return USER_NOT_AVAILABLE;
    }
    return new ThymeleafMessage(BY_USER_KEY, event.user());
  }

  private static List<ThymeleafMessage> toDescriptions(ClaimHistoryEvent event) {
    return switch (event.type()) {
      case CLAIM_CREATED -> List.of(CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION);
      case CLAIM_CREATED_AND_ESCAPED ->
          List.of(
              CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION, CLAIM_ESCAPED_DESCRIPTION);
      case CLAIM_ASSESSED_ESCAPE_CASE ->
          toAssessmentDescription(CLAIM_ASSESSED_ESCAPE_CASE_DESCRIPTION_KEY, event);
      case CLAIM_ASSESSED_STAGE_DISBURSEMENT ->
          toAssessmentDescription(CLAIM_ASSESSED_STAGE_DISBURSEMENT_DESCRIPTION_KEY, event);
      case CLAIM_VOIDED -> List.of(CLAIM_VOIDED_DESCRIPTION);
    };
  }

  private static List<ThymeleafMessage> toAssessmentDescription(
      String descriptionKey, ClaimHistoryEvent event) {
    var outcomeType =
        event
            .outcomeType()
            .orElseThrow(() -> new RuntimeException("Assessment outcome type not found"));
    return List.of(
        new ThymeleafMessage(descriptionKey, new ThymeleafMessage(outcomeType.getMessageKey())));
  }
}
