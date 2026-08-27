package uk.gov.justice.laa.payments.amend.service;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_ESCAPE_CASE;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_ASSESSED_STAGE_DISBURSEMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_CREATED;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_CREATED_AND_ESCAPED;
import static uk.gov.justice.laa.payments.amend.models.enums.ClaimHistoryEventType.CLAIM_VOIDED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.amend.claim.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistory;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryAssessedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryCreatedEvent;
import uk.gov.justice.laa.amend.claim.models.history.ClaimHistoryVoidedEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.models.AmendmentConfirmation;
import uk.gov.justice.laa.payments.amend.models.AssessmentInfo;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimHistorySummary;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;
import uk.gov.justice.laadata.providers.model.ProviderFirmOfficeDto;
import uk.gov.justice.laadata.providers.model.ProviderFirmSummary;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimHistoryService {

  public static final int MAXIMUM_ASSESSMENTS = 100;

  private final AssessmentService assessmentService;
  private final ClaimsApiClient claimsApiClient;
  private final FeatureFlagsConfig featureFlagsConfig;
  private final ProviderService providerService;
  private final UserRetrievalService userRetrievalService;
  private final SystemReferenceService systemReferenceService;

  private static final Map<AreaOfLaw, Map<String, ClaimViewField<?>>>
      AMENDABLE_FIELDS_BY_IDENTIFIER =
          Map.of(
              AreaOfLaw.CRIME_LOWER, buildFieldLookup(AreaOfLaw.CRIME_LOWER),
              AreaOfLaw.LEGAL_HELP, buildFieldLookup(AreaOfLaw.LEGAL_HELP),
              AreaOfLaw.MEDIATION, buildFieldLookup(AreaOfLaw.MEDIATION));

  public ClaimHistory getClaimHistory(ClaimDetails claim) {
    List<AssessmentInfo> assessments =
        claim.isHasAssessment()
            ? assessmentService.getLatestAssessmentsByClaim(claim.getClaimId(), MAXIMUM_ASSESSMENTS)
            : List.of();

    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    var userIdToUser = getUserIdToUser(assessments);

    var events =
        Stream.of(
                Stream.of(getClaimCreatedEvent(claim)),
                toClaimHistoryEvents(assessments, userIdToUser),
                toAmendmentClaimHistoryEvents(history, claim))
            .flatMap(s -> s)
            .sorted(comparing(BaseClaimHistoryEvent::eventDateTime).reversed())
            .toList();

    var latestAssessmentUser =
        assessments.stream().findFirst().map(AssessmentInfo::lastAssessedBy).map(userIdToUser::get);

    return new ClaimHistory(events, latestAssessmentUser);
  }

  public ClaimHistorySummary getClaimHistorySummary(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return ClaimHistorySummary.builder()
          .lastUpdatedUser(userRetrievalService.getUser(claim.getLastUpdatedUser()))
          .lastUpdatedDateTime(claim.getLastUpdatedDateTime())
          .amendedFields(Set.of())
          .build();
    }

    var builder = ClaimHistorySummary.builder();
    var latestEvent = getLatestRelevantEvent(claim, history);
    latestEvent.ifPresent(
        event -> {
          builder.lastUpdatedDateTime(event.getEventTimestamp());
          builder.lastUpdatedUser(userRetrievalService.getUser(event.getActorId()));
        });

    if (claim.isAmended()) {
      builder.amendedFields(
          history.getEvents().stream()
              .filter(event -> event.getEventType() == AMENDMENT)
              .map(ClaimHistoryService::getChanges)
              .flatMap(Collection::stream)
              .filter(ClaimHistoryService::isRequested)
              .map(ClaimHistoryService::getFieldIdentifier)
              .collect(toSet()));
    } else {
      builder.amendedFields(Set.of());
    }

    return builder.build();
  }

  private static Optional<uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent>
      getLatestRelevantEvent(ClaimDetails claim, ClaimHistoryResultSet claimHistoryResultSet) {
    if (claim.getDerivedClaimStatus() == null) {
      return Optional.empty();
    }

    var eventType =
        switch (claim.getDerivedClaimStatus()) {
          case ACCEPTED -> ClaimHistoryEventType.SUBMISSION;
          case AMENDED -> ClaimHistoryEventType.AMENDMENT;
          case ASSESSED -> ClaimHistoryEventType.ASSESSMENT;
          case VOIDED -> ClaimHistoryEventType.VOID;
          default -> null;
        };

    if (eventType == null) {
      return Optional.empty();
    }

    return claimHistoryResultSet.getEvents().stream()
        .filter(event -> event.getEventType() == eventType)
        .findFirst();
  }

  public AmendmentConfirmation getAmendmentConfirmation(ClaimDetails claim) {
    var history = claimsApiClient.getClaimHistory(claim.getClaimId()).block();

    if (history == null || history.getEvents() == null) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var amendmentEvent =
        history.getEvents().stream().filter(event -> event.getEventType() == AMENDMENT).findFirst();

    if (amendmentEvent.isEmpty()) {
      log.error("Could not get claim history for claim {}", claim.getClaimId());
      return new AmendmentConfirmation(false, Set.of());
    }

    var amendment = amendmentEvent.get();

    if (!TRUE.equals(amendment.getMetadata().get("price_changed"))) {
      return new AmendmentConfirmation(false, Set.of());
    }

    var changedCalculatedCosts =
        getChanges(amendment).stream()
            .map(ClaimHistoryService::getFieldIdentifier)
            .collect(toSet());

    return new AmendmentConfirmation(true, changedCalculatedCosts);
  }

  private Map<String, MicrosoftApiUser> getUserIdToUser(final List<AssessmentInfo> assessments) {
    var userIds =
        assessments.stream()
            .map(AssessmentInfo::lastAssessedBy)
            .filter(Objects::nonNull)
            .collect(toSet());
    var userIdToUser = new HashMap<String, MicrosoftApiUser>();
    userIds.forEach(
        userId -> {
          var user = userRetrievalService.getUser(userId);
          if (user != null) {
            userIdToUser.put(userId, user);
          }
        });
    return userIdToUser;
  }

  private BaseClaimHistoryEvent getClaimCreatedEvent(ClaimDetails claim) {
    var user = getClaimCreatedUser(claim);
    return new ClaimHistoryCreatedEvent(
        claim.getSubmittedDate(), user, TRUE.equals(claim.getEscaped()));
  }

  private String getClaimCreatedUser(ClaimDetails claim) {
    return Optional.ofNullable(providerService.getProviderFirm(claim.getOfficeCode()))
        .map(ProviderFirmOfficeDto::getFirm)
        .map(ProviderFirmSummary::getFirmName)
        .orElse(claim.getOfficeCode());
  }

  private static Stream<BaseClaimHistoryEvent> toClaimHistoryEvents(
      List<AssessmentInfo> assessments, Map<String, MicrosoftApiUser> userIdToUser) {
    return assessments.stream().map(assessment -> toClaimHistoryEvent(assessment, userIdToUser));
  }

  private static BaseClaimHistoryEvent toClaimHistoryEvent(
      AssessmentInfo assessment, Map<String, MicrosoftApiUser> userIdToUser) {
    var userName =
        Optional.ofNullable(userIdToUser.get(assessment.lastAssessedBy()))
            .map(MicrosoftApiUser::name)
            .orElse(null);

    return switch (assessment.assessmentType()) {
      case ESCAPE_CASE_ASSESSMENT, STAGE_DISBURSEMENT_ASSESSMENT ->
          new ClaimHistoryAssessedEvent(
              assessment.lastAssessmentDate(),
              userName,
              assessment.assessmentType(),
              assessment.lastAssessmentOutcome());
      case VOID -> new ClaimHistoryVoidedEvent(assessment.lastAssessmentDate(), userName);
    };
  }

  private Stream<BaseClaimHistoryEvent> toAmendmentClaimHistoryEvents(
      ClaimHistoryResultSet history, ClaimDetails claim) {
    if (history == null || history.getEvents() == null) {
      return Stream.empty();
    }
    var requestedByReferenceList = systemReferenceService.getAmendmentRequestedByReferenceList();
    return history.getEvents().stream()
        .filter(e -> e.getEventType() == AMENDMENT)
        .map(e -> toAmendmentClaimHistoryEvent(e, claim, requestedByReferenceList));
  }

  private BaseClaimHistoryEvent toAmendmentClaimHistoryEvent(
      uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent apiEvent,
      ClaimDetails claim,
      AmendmentRequestedByReferenceList requestedByReferenceList) {
    var user =
        Optional.ofNullable(apiEvent.getActorId())
            .map(userRetrievalService::getUser)
            .map(MicrosoftApiUser::name)
            .orElse(null);

    var metadata = Optional.ofNullable(apiEvent.getMetadata()).orElse(Map.<String, Object>of());
    var changes = resolveAmendmentChanges(metadata, claim.getAreaOfLaw());
    var requestedByCode = toFallbackString(metadata.get("requested_by_code"));
    var amendmentReasonCode = toFallbackString(metadata.get("amendment_reason_code"));
    var requestedByDisplay = resolveRequestedByDisplay(requestedByCode, requestedByReferenceList);
    var amendmentReasonDisplay =
        resolveAmendmentReasonDisplay(
            requestedByCode, amendmentReasonCode, requestedByReferenceList);

    return new ClaimHistoryAmendedEvent(
        apiEvent.getEventTimestamp(), user, changes, requestedByDisplay, amendmentReasonDisplay);
  }

  @SuppressWarnings("unchecked")
  private List<ClaimHistoryAmendmentChange> resolveAmendmentChanges(
      Map<String, Object> metadata, AreaOfLaw areaOfLaw) {
    var rawChanges = (List<Map<String, Object>>) metadata.getOrDefault("changes", List.of());
    return rawChanges.stream()
        .filter(c -> "REQUESTED".equals(c.get("change_source")))
        .map(c -> resolveChange(c, areaOfLaw))
        .toList();
  }

  private ClaimHistoryAmendmentChange resolveChange(
      Map<String, Object> rawChange, AreaOfLaw areaOfLaw) {
    var fieldIdentifier = (String) rawChange.get("field_identifier");
    var fieldOpt = resolveField(areaOfLaw, fieldIdentifier);

    if (fieldOpt.isEmpty()) {
      log.warn(
          "Unknown amendment field identifier '{}' for area of law {}", fieldIdentifier, areaOfLaw);
      return new ClaimHistoryAmendmentChange(
          null,
          fieldIdentifier,
          toFallbackString(rawChange.get("before")),
          toFallbackString(rawChange.get("after")));
    }

    var field = fieldOpt.get();
    return new ClaimHistoryAmendmentChange(
        field,
        fieldIdentifier,
        resolveValue(rawChange.get("before"), field),
        resolveValue(rawChange.get("after"), field));
  }

  private static Optional<ClaimViewField<?>> resolveField(
      AreaOfLaw areaOfLaw, String fieldIdentifier) {
    if (areaOfLaw == null || fieldIdentifier == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(AMENDABLE_FIELDS_BY_IDENTIFIER.get(areaOfLaw))
        .map(fields -> fields.get(fieldIdentifier));
  }

  private static Object resolveValue(Object raw, ClaimViewField<?> field) {
    if (raw == null) {
      return null;
    }
    var fieldType = field.getFieldType();
    try {
      return switch (fieldType) {
        case TEXT -> String.valueOf(raw);
        case BOOLEAN -> raw instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(raw));
        case NUMBER ->
            raw instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(raw));
        case BIG_DECIMAL -> new BigDecimal(String.valueOf(raw));
        case DATE -> LocalDate.parse(String.valueOf(raw));
        case ENUM -> resolveEnumValue(raw, field.getOptions());
      };
    } catch (Exception e) {
      log.warn("Failed to resolve value '{}' as field type {}: {}", raw, fieldType, e.getMessage());
    }
    return raw;
  }

  private static Object resolveEnumValue(Object raw, List<FieldOption> options) {
    if (options == null || options.isEmpty()) {
      return String.valueOf(raw);
    }
    var rawValue = String.valueOf(raw);
    return options.stream()
        .filter(option -> option.value().equals(rawValue))
        .findFirst()
        .orElse(null);
  }

  private static String toFallbackString(Object rawValue) {
    return rawValue == null ? null : String.valueOf(rawValue);
  }

  private static Map<String, ClaimViewField<?>> buildFieldLookup(AreaOfLaw areaOfLaw) {
    var lookup = new LinkedHashMap<String, ClaimViewField<?>>();
    areaViewFields(areaOfLaw).forEach(field -> putFieldIdentifiers(lookup, field));
    return Map.copyOf(lookup);
  }

  private static Stream<ClaimViewField<?>> areaViewFields(AreaOfLaw areaOfLaw) {
    var commonFields =
        Arrays.stream(ClaimDetailsViewField.values()).map(field -> (ClaimViewField<?>) field);

    var areaSpecificFields =
        switch (areaOfLaw) {
          case CRIME_LOWER -> Arrays.stream(CrimeClaimDetailsViewField.values());
          case LEGAL_HELP -> Arrays.stream(CivilClaimDetailsViewField.values());
          case MEDIATION -> Arrays.stream(MediationClaimDetailsViewField.values());
        };

    return Stream.concat(commonFields, areaSpecificFields.map(field -> (ClaimViewField<?>) field));
  }

  private static void putFieldIdentifiers(
      Map<String, ClaimViewField<?>> fieldLookup, ClaimViewField<?> field) {
    putFieldIdentifier(fieldLookup, field.getClaimsApiFieldName(), field);
    putFieldIdentifier(fieldLookup, field.getFeeApiFieldName(), field);
  }

  private static void putFieldIdentifier(
      Map<String, ClaimViewField<?>> fieldLookup, String identifier, ClaimViewField<?> field) {
    if (identifier == null || identifier.isBlank()) {
      return;
    }
    fieldLookup.putIfAbsent(identifier, field);
  }

  private String resolveRequestedByDisplay(
      String requestedByCode, AmendmentRequestedByReferenceList requestedByReferenceList) {
    if (requestedByCode == null) {
      return null;
    }
    var requestedByMap =
        Optional.ofNullable(
                systemReferenceService.getAmendmentRequestedByOptions(requestedByReferenceList))
            .orElse(Map.of());
    return requestedByMap.getOrDefault(requestedByCode, requestedByCode);
  }

  private String resolveAmendmentReasonDisplay(
      String requestedByCode,
      String amendmentReasonCode,
      AmendmentRequestedByReferenceList requestedByReferenceList) {
    if (amendmentReasonCode == null) {
      return null;
    }
    var amendmentReasonMap =
        Optional.ofNullable(
                systemReferenceService.getAmendmentRequestReason(
                    requestedByCode, requestedByReferenceList))
            .orElse(Map.of());
    return amendmentReasonMap.getOrDefault(amendmentReasonCode, amendmentReasonCode);
  }

  @SuppressWarnings("unchecked")
  private static List<LinkedHashMap<String, String>> getChanges(
      uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent claimHistoryEvent) {
    return ((List<LinkedHashMap<String, String>>)
        claimHistoryEvent.getMetadata().getOrDefault("changes", List.of()));
  }

  private static boolean isRequested(LinkedHashMap<String, String> change) {
    return "REQUESTED".equals(change.get("change_source"));
  }

  private static String getFieldIdentifier(LinkedHashMap<String, String> change) {
    return change.get("field_identifier");
  }
}
