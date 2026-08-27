package uk.gov.justice.laa.amend.claim.viewmodels.history;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.amend.claim.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

public record ClaimHistoryEventViewModel(
    ThymeleafMessage type,
    OffsetDateTime eventDateTime,
    ThymeleafString user,
    List<ThymeleafString> descriptions,
    List<ClaimHistoryAmendmentChangeViewModel> amendmentChanges,
    ThymeleafString amendmentRequestedBy,
    ThymeleafString amendmentReason,
    boolean amendmentEvent) {

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

  public static ClaimHistoryEventViewModel create(
      BaseClaimHistoryEvent event, MessageSource messageSource, Locale locale) {
    return new ClaimHistoryEventViewModel(
        toTypeMessage(event),
        event.eventDateTime(),
        toUserMessage(event),
        toDescriptions(event, messageSource, locale),
        toAmendmentChanges(event, messageSource, locale),
        toAmendmentRequestedBy(event),
        toAmendmentReason(event),
        event instanceof ClaimHistoryAmendedEvent);
  }

  private static ThymeleafMessage toTypeMessage(BaseClaimHistoryEvent event) {
    if (event instanceof ClaimHistoryCreatedEvent) {
      return new ThymeleafMessage("claimHistory.claimCreated.type");
    }
    if (event instanceof ClaimHistoryAmendedEvent) {
      return new ThymeleafMessage("claimHistory.claimAmended.type");
    }
    if (event instanceof ClaimHistoryVoidedEvent) {
      return new ThymeleafMessage("claimHistory.claimVoided.type");
    }
    if (event instanceof ClaimHistoryAssessedEvent assessedEvent) {
      return new ThymeleafMessage(
          assessedEvent.assessmentType() == AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT
              ? "claimHistory.claimAssessedEscapeCase.type"
              : "claimHistory.claimAssessedStageDisbursement.type");
    }
    throw new RuntimeException(
        "Unknown claim history event type: " + event.getClass().getSimpleName());
  }

  private static ThymeleafString toUserMessage(BaseClaimHistoryEvent event) {
    if (event.user() == null) {
      return USER_NOT_AVAILABLE;
    }
    return new ThymeleafMessage(BY_USER_KEY, event.user());
  }

  private static List<ThymeleafString> toDescriptions(
      BaseClaimHistoryEvent event, MessageSource messageSource, Locale locale) {
    if (event instanceof ClaimHistoryCreatedEvent createdEvent) {
      return createdEvent.escaped()
          ? List.of(
              CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION, CLAIM_ESCAPED_DESCRIPTION)
          : List.of(CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION);
    }
    if (event instanceof ClaimHistoryAmendedEvent amendedEvent) {
      return List.of();
    }
    if (event instanceof ClaimHistoryVoidedEvent) {
      return List.of(CLAIM_VOIDED_DESCRIPTION);
    }
    if (event instanceof ClaimHistoryAssessedEvent assessedEvent) {
      var descriptionKey =
          assessedEvent.assessmentType() == AssessmentTypeEnum.ESCAPE_CASE_ASSESSMENT
              ? CLAIM_ASSESSED_ESCAPE_CASE_DESCRIPTION_KEY
              : CLAIM_ASSESSED_STAGE_DISBURSEMENT_DESCRIPTION_KEY;
      return toAssessmentDescription(descriptionKey, assessedEvent);
    }
    throw new RuntimeException(
        "Unknown claim history event type: " + event.getClass().getSimpleName());
  }

  private static List<ThymeleafString> toAssessmentDescription(
      String descriptionKey, ClaimHistoryAssessedEvent event) {
    return List.of(
        new ThymeleafMessage(
            descriptionKey, new ThymeleafMessage(event.outcomeType().getMessageKey())));
  }

  private static List<ClaimHistoryAmendmentChangeViewModel> toAmendmentChanges(
      BaseClaimHistoryEvent event, MessageSource messageSource, Locale locale) {
    if (!(event instanceof ClaimHistoryAmendedEvent amendedEvent)) {
      return List.of();
    }
    return toAmendmentChanges(amendedEvent, messageSource, locale);
  }

  private static List<ClaimHistoryAmendmentChangeViewModel> toAmendmentChanges(
      ClaimHistoryAmendedEvent event, MessageSource messageSource, Locale locale) {
    return event.amendmentChanges().stream()
        .sorted(
            Comparator.comparing(
                change -> resolvedFieldLabel(change, messageSource, locale),
                String.CASE_INSENSITIVE_ORDER))
        .map(
            change ->
                new ClaimHistoryAmendmentChangeViewModel(
                    toAmendmentFieldLabel(change), change.before(), change.after()))
        .toList();
  }

  private static String resolvedFieldLabel(
      ClaimHistoryAmendmentChange change, MessageSource messageSource, Locale locale) {
    return Optional.ofNullable(change.fieldMessageKey())
        .map(key -> messageSource.getMessage(key, null, change.fieldIdentifier(), locale))
        .orElse(change.fieldIdentifier());
  }

  private static ThymeleafString toAmendmentFieldLabel(ClaimHistoryAmendmentChange change) {
    return Optional.ofNullable(change.fieldMessageKey())
        .<ThymeleafString>map(ThymeleafMessage::new)
        .orElseGet(() -> new ThymeleafLiteralString(change.fieldIdentifier()));
  }

  private static ThymeleafString toAmendmentRequestedBy(BaseClaimHistoryEvent event) {
    if (!(event instanceof ClaimHistoryAmendedEvent amendedEvent)) {
      return null;
    }
    return amendedEvent.requestedByCode() == null
        ? new ThymeleafMessage("service.noData")
        : new ThymeleafLiteralString(amendedEvent.requestedByCode());
  }

  private static ThymeleafString toAmendmentReason(BaseClaimHistoryEvent event) {
    if (!(event instanceof ClaimHistoryAmendedEvent amendedEvent)) {
      return null;
    }
    return amendedEvent.amendmentReasonCode() == null
        ? new ThymeleafMessage("service.noData")
        : new ThymeleafLiteralString(amendedEvent.amendmentReasonCode());
  }
}
