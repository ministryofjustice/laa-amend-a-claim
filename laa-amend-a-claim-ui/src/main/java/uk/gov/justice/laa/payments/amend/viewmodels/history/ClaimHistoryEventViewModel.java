package uk.gov.justice.laa.payments.amend.viewmodels.history;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryFspEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.payments.amend.utils.CurrencyUtils;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;

public record ClaimHistoryEventViewModel(
    ThymeleafMessage type,
    OffsetDateTime eventDateTime,
    ThymeleafString user,
    List<ThymeleafString> descriptions,
    List<ClaimHistoryAmendmentChangeViewModel> amendmentChanges,
    ThymeleafString amendmentRequestedBy,
    ThymeleafString amendmentReason,
    boolean amendmentEvent,
    boolean fspEvent,
    ThymeleafString fspTotalChangedDescription,
    List<ClaimHistoryAmendmentChangeViewModel> fspRecalculatedChanges) {

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
      BaseClaimHistoryEvent event, MessageSource messageSource) {
    return new ClaimHistoryEventViewModel(
        toTypeMessage(event),
        event.eventDateTime(),
        toUserMessage(event),
        toDescriptions(event, messageSource),
        toAmendmentChanges(event, messageSource),
        toAmendmentRequestedBy(event),
        toAmendmentReason(event),
        event instanceof ClaimHistoryAmendedEvent,
        event instanceof ClaimHistoryFspEvent,
        toFspTotalChangedDescription(event),
        toFspRecalculatedChanges(event, messageSource));
  }

  private static ThymeleafMessage toTypeMessage(BaseClaimHistoryEvent event) {
    if (event instanceof ClaimHistoryCreatedEvent) {
      return new ThymeleafMessage("claimHistory.claimCreated.type");
    }
    if (event instanceof ClaimHistoryAmendedEvent) {
      return new ThymeleafMessage("claimHistory.claimAmended.type");
    }
    if (event instanceof ClaimHistoryFspEvent) {
      return new ThymeleafMessage("claimHistory.claimFspRecalculated.type");
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
    if (event instanceof ClaimHistoryFspEvent) {
      return new ThymeleafMessage("claimHistory.claimFspRecalculated.byUser");
    }
    if (event.user() == null) {
      return USER_NOT_AVAILABLE;
    }
    return new ThymeleafMessage(BY_USER_KEY, event.user());
  }

  private static List<ThymeleafString> toDescriptions(
      BaseClaimHistoryEvent event, MessageSource messageSource) {
    if (event instanceof ClaimHistoryCreatedEvent createdEvent) {
      return createdEvent.escaped()
          ? List.of(
              CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION, CLAIM_ESCAPED_DESCRIPTION)
          : List.of(CLAIM_CREATED_DESCRIPTION, CLAIM_CALCULATED_DESCRIPTION);
    }
    if (event instanceof ClaimHistoryAmendedEvent amendedEvent) {
      return List.of();
    }
    if (event instanceof ClaimHistoryFspEvent) {
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
      BaseClaimHistoryEvent event, MessageSource messageSource) {
    if (!(event instanceof ClaimHistoryAmendedEvent amendedEvent)) {
      return List.of();
    }
    return toAmendmentChanges(amendedEvent, messageSource);
  }

  private static List<ClaimHistoryAmendmentChangeViewModel> toAmendmentChanges(
      ClaimHistoryAmendedEvent event, MessageSource messageSource) {
    return event.amendmentChanges().stream()
        .sorted(
            Comparator.comparing(
                change -> resolvedFieldLabel(change, messageSource), String.CASE_INSENSITIVE_ORDER))
        .map(
            change ->
                new ClaimHistoryAmendmentChangeViewModel(
                    change.field(),
                    toAmendmentFieldLabel(change, messageSource),
                    change.before(),
                    change.after()))
        .toList();
  }

  private static String resolvedFieldLabel(
      ClaimHistoryAmendmentChange change, MessageSource messageSource) {

    // If we weren't able to resolve a field for the change, just return the identifier from the
    // history API
    if (change.field() == null) {
      return change.fieldIdentifier();
    }

    var messageKeys = new ArrayList<String>();

    // field label should be lowercase in claim history bullet list
    var lowercaseKey = "claimField." + change.field().name() + ".lower";

    // some fields may require a different wording when they appear on the claim history page
    // without the usual context we have on the Claim Details pages
    // e.g. instead of "first name" we need "client first name"
    var standaloneKey = "claimField." + change.field().name() + ".standaloneLower";

    // some fields may require a different wording for a specific area of law
    // e.g. instead of "first name" we need "client 1 first name"
    if (change.areaOfLaw() != null) {
      messageKeys.add(standaloneKey + "." + change.areaOfLaw().name());
    }

    messageKeys.add(standaloneKey);
    messageKeys.add(lowercaseKey);

    String[] codesArray = messageKeys.toArray(new String[0]);
    return messageSource.getMessage(
        new DefaultMessageSourceResolvable(codesArray, null, change.fieldIdentifier()), Locale.UK);
  }

  private static ThymeleafString toAmendmentFieldLabel(
      ClaimHistoryAmendmentChange change, MessageSource messageSource) {
    return new ThymeleafLiteralString(resolvedFieldLabel(change, messageSource));
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

  private static ThymeleafString toFspTotalChangedDescription(BaseClaimHistoryEvent event) {
    if (!(event instanceof ClaimHistoryFspEvent fspEvent)) {
      return null;
    }
    if (fspEvent.totalBefore() == null || fspEvent.totalAfter() == null) {
      return null;
    }
    return new ThymeleafMessage(
        "claimHistory.claimFspRecalculated.totalChanged",
        new ThymeleafLiteralString(CurrencyUtils.formatCurrency(fspEvent.totalBefore())),
        new ThymeleafLiteralString(CurrencyUtils.formatCurrency(fspEvent.totalAfter())));
  }

  private static List<ClaimHistoryAmendmentChangeViewModel> toFspRecalculatedChanges(
      BaseClaimHistoryEvent event, MessageSource messageSource) {
    if (!(event instanceof ClaimHistoryFspEvent fspEvent)) {
      return List.of();
    }
    return fspEvent.recalculatedChanges().stream()
        .sorted(
            Comparator.comparing(
                change -> resolvedFieldLabel(change, messageSource), String.CASE_INSENSITIVE_ORDER))
        .map(
            change ->
                new ClaimHistoryAmendmentChangeViewModel(
                    change.field(),
                    toAmendmentFieldLabel(change, messageSource),
                    change.before(),
                    change.after()))
        .toList();
  }
}
