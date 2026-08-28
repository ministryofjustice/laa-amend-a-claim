package uk.gov.justice.laa.payments.amend.service;

import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.ASSESSMENT;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.SUBMISSION;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.VOID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAmendmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAssessmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryChangeEntry;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistorySubmissionMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryVoidMetadata;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryApiEvent;

@Slf4j
final class ClaimHistoryMetadataMapper {

  private ClaimHistoryMetadataMapper() {}

  static List<ClaimHistoryApiEvent> toApiEvents(ClaimHistoryResultSet history) {
    if (history == null || history.getEvents() == null) {
      return List.of();
    }
    return history.getEvents().stream().map(ClaimHistoryMetadataMapper::toApiEvent).toList();
  }

  private static ClaimHistoryApiEvent toApiEvent(ClaimHistoryEvent event) {
    var eventType = event.getEventType();
    return new ClaimHistoryApiEvent(
        eventType,
        event.getEventTimestamp(),
        event.getActorId(),
        event.getSourceId(),
        eventType == SUBMISSION ? toSubmissionMetadata(event) : null,
        eventType == AMENDMENT ? toAmendmentMetadata(event) : null,
        eventType == ASSESSMENT ? toAssessmentMetadata(event) : null,
        eventType == VOID ? toVoidMetadata(event) : null);
  }

  static ClaimHistorySubmissionMetadata toSubmissionMetadata(ClaimHistoryEvent event) {
    var metadata = getMetadata(event);
    return new ClaimHistorySubmissionMetadata()
        .submissionPeriod(asString(getRequiredValue(metadata, "submission_period")))
        .officeAccountNumber(asString(getRequiredValue(metadata, "office_account_number")))
        .areaOfLaw(asString(getRequiredValue(metadata, "area_of_law")));
  }

  static ClaimHistoryAssessmentMetadata toAssessmentMetadata(ClaimHistoryEvent event) {
    var metadata = getMetadata(event);
    return new ClaimHistoryAssessmentMetadata()
        .assessmentType(asString(getRequiredValue(metadata, "assessment_type")))
        .assessmentOutcome(asString(getRequiredValue(metadata, "assessment_outcome")))
        .assessmentReason(asString(getRequiredValue(metadata, "assessment_reason")));
  }

  static ClaimHistoryVoidMetadata toVoidMetadata(ClaimHistoryEvent event) {
    var metadata = getMetadata(event);
    return new ClaimHistoryVoidMetadata()
        .assessmentType(asString(getRequiredValue(metadata, "assessment_type")))
        .assessmentReason(asString(getRequiredValue(metadata, "assessment_reason")));
  }

  static ClaimHistoryAmendmentMetadata toAmendmentMetadata(ClaimHistoryEvent event) {
    var metadata = getMetadata(event);
    return new ClaimHistoryAmendmentMetadata()
        .requestedByCode(asString(getRequiredValue(metadata, "requested_by_code")))
        .amendmentReasonCode(asString(getRequiredValue(metadata, "amendment_reason_code")))
        .pricingRecalculated(asBoolean(getRequiredValue(metadata, "pricing_recalculated")))
        .priceChanged(asBoolean(getRequiredValue(metadata, "price_changed")))
        .escapeCaseLogged(asBoolean(getRequiredValue(metadata, "escape_case_logged")))
        .changes(toChanges(getRequiredValue(metadata, "changes")));
  }

  private static Map<String, Object> getMetadata(ClaimHistoryEvent event) {
    if (event == null || event.getMetadata() == null) {
      log.warn("No metadata found for event '{}'", event);
      return Map.of();
    }
    return event.getMetadata();
  }

  private static List<ClaimHistoryChangeEntry> toChanges(Object rawChanges) {
    if (!(rawChanges instanceof List<?> entries)) {
      log.warn("Invalid list entries found for '{}'", rawChanges);
      return List.of();
    }

    var changes = new ArrayList<ClaimHistoryChangeEntry>();
    entries.stream()
        .filter(Map.class::isInstance)
        .map(Map.class::cast)
        .map(ClaimHistoryMetadataMapper::toChange)
        .forEach(changes::add);
    return List.copyOf(changes);
  }

  private static ClaimHistoryChangeEntry toChange(Map<?, ?> rawChange) {
    return new ClaimHistoryChangeEntry()
        .fieldIdentifier(asString(rawChange.get("field_identifier")))
        .changeSource(toChangeSource(rawChange.get("change_source")))
        .before(rawChange.get("before"))
        .after(rawChange.get("after"));
  }

  private static ClaimHistoryChangeEntry.ChangeSourceEnum toChangeSource(Object rawSource) {
    var source = asString(rawSource);
    if (source == null) {
      log.warn("No source found for '{}'", rawSource);
      return null;
    }
    try {
      return ClaimHistoryChangeEntry.ChangeSourceEnum.fromValue(source);
    } catch (IllegalArgumentException ignored) {
      log.warn("Invalid change source found for '{}'", rawSource);
      return null;
    }
  }

  private static String asString(Object raw) {
    return raw == null ? null : String.valueOf(raw);
  }

  private static Boolean asBoolean(Object raw) {
    if (raw instanceof Boolean value) {
      return value;
    }
    if (raw instanceof String value) {
      return Boolean.valueOf(value);
    }
    log.warn("Invalid boolean value found for '{}'", raw);
    return null;
  }

  private static Object getRequiredValue(Map<String, Object> metadata, String key) {
    var value = metadata.get(key);
    if (value == null) {
      log.warn("No value found for key '{}' in metadata '{}'", key, metadata);
      return null;
    }
    return value;
  }
}
