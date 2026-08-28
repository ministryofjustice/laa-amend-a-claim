package uk.gov.justice.laa.payments.amend.service;

import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;
import static uk.gov.justice.laa.payments.amend.models.enums.Amendability.NEVER;
import static uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw.CRIME_LOWER;
import static uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw.LEGAL_HELP;
import static uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw.MEDIATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.models.history.BaseClaimHistoryEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendmentChange;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.FieldOption;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimHistoryAmendmentsService {

  private static final String CHANGE_SOURCE_REQUESTED = "REQUESTED";
  private static final String CHANGE_SOURCE_FSP = "FSP";
  private static final String FIELD_IDENTIFIER_FEE_CODE = "claim.feeCode";
  private static final String FIELD_IDENTIFIER_MATTER_TYPE_CODE = "claim.matterTypeCode";

  private final UserRetrievalService userRetrievalService;
  private final SystemReferenceService systemReferenceService;
  private final AvailableFeeCodesService availableFeeCodesService;

  private static final Map<AreaOfLaw, Map<String, ClaimViewField<?>>>
      AMENDABLE_FIELDS_BY_IDENTIFIER =
          Map.of(
              CRIME_LOWER, viewFieldsByAreaOfLaw(CRIME_LOWER),
              LEGAL_HELP, viewFieldsByAreaOfLaw(LEGAL_HELP),
              MEDIATION, viewFieldsByAreaOfLaw(MEDIATION));

  public Stream<BaseClaimHistoryEvent> toAmendmentClaimHistoryEvents(
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
      ClaimHistoryEvent apiEvent,
      ClaimDetails claim,
      AmendmentRequestedByReferenceList requestedByReferenceList) {
    var user =
        Optional.ofNullable(apiEvent.getActorId())
            .map(userRetrievalService::getUser)
            .map(MicrosoftApiUser::name)
            .orElse(null);

    var metadata = Optional.ofNullable(apiEvent.getMetadata()).orElse(Map.of());
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
    var changes = (List<Map<String, Object>>) metadata.getOrDefault("changes", List.of());
    var availableFeeCodes = resolveAvailableFeeCodes(changes, areaOfLaw);
    return changes.stream()
        .filter(ClaimHistoryAmendmentsService::isDisplayableChange)
        .flatMap(change -> resolveChanges(change, areaOfLaw, availableFeeCodes).stream())
        .toList();
  }

  private Map<String, String> resolveAvailableFeeCodes(
      List<Map<String, Object>> changes, AreaOfLaw areaOfLaw) {
    if (areaOfLaw == null || changes.stream().noneMatch(this::isFeeCodeChange)) {
      return Map.of();
    }
    return availableFeeCodesService.getAvailableFeeCodes(areaOfLaw);
  }

  private static boolean isDisplayableChange(Map<String, Object> change) {
    var source = toFallbackString(change.get("change_source"));
    if (CHANGE_SOURCE_REQUESTED.equals(source)) {
      return true;
    }
    if (!CHANGE_SOURCE_FSP.equals(source)) {
      return false;
    }
    return FIELD_IDENTIFIER_FEE_CODE.equals(change.get("field_identifier"));
  }

  private boolean isFeeCodeChange(Map<String, Object> change) {
    return isDisplayableChange(change)
        && FIELD_IDENTIFIER_FEE_CODE.equals(change.get("field_identifier"));
  }

  private ClaimHistoryAmendmentChange resolveChange(
      Map<String, Object> change, AreaOfLaw areaOfLaw, Map<String, String> availableFeeCodes) {
    var fieldIdentifier = (String) change.get("field_identifier");
    var fieldOpt = resolveField(areaOfLaw, fieldIdentifier);

    if (fieldOpt.isEmpty()) {
      log.warn(
          "Unknown amendment field identifier '{}' for area of law {}", fieldIdentifier, areaOfLaw);
      return new ClaimHistoryAmendmentChange(
          null,
          fieldIdentifier,
          toFallbackString(change.get("before")),
          toFallbackString(change.get("after")),
          areaOfLaw);
    }

    var field = fieldOpt.get();
    return new ClaimHistoryAmendmentChange(
        field,
        fieldIdentifier,
        resolveValue(change.get("before"), field, availableFeeCodes),
        resolveValue(change.get("after"), field, availableFeeCodes),
        areaOfLaw);
  }

  private List<ClaimHistoryAmendmentChange> resolveChanges(
      Map<String, Object> change, AreaOfLaw areaOfLaw, Map<String, String> availableFeeCodes) {
    var fieldIdentifier = toFallbackString(change.get("field_identifier"));
    if (FIELD_IDENTIFIER_MATTER_TYPE_CODE.equals(fieldIdentifier)) {
      return resolveMatterTypeChanges(change, areaOfLaw, availableFeeCodes);
    }
    return List.of(resolveChange(change, areaOfLaw, availableFeeCodes));
  }

  private List<ClaimHistoryAmendmentChange> resolveMatterTypeChanges(
      Map<String, Object> change, AreaOfLaw areaOfLaw, Map<String, String> availableFeeCodes) {
    var beforeParts = splitMatterTypeCode(change.get("before"));
    var afterParts = splitMatterTypeCode(change.get("after"));
    var resolvedChanges = new ArrayList<ClaimHistoryAmendmentChange>();

    var firstMatterTypeIndex = 0;
    addMatterTypeChangeIfChanged(
        resolvedChanges,
        areaOfLaw,
        availableFeeCodes,
        beforeParts,
        afterParts,
        firstMatterTypeIndex);

    var secondMatterTypeIndex = 1;
    addMatterTypeChangeIfChanged(
        resolvedChanges,
        areaOfLaw,
        availableFeeCodes,
        beforeParts,
        afterParts,
        secondMatterTypeIndex);

    if (!resolvedChanges.isEmpty()) {
      return List.copyOf(resolvedChanges);
    }

    return List.of(resolveChange(change, areaOfLaw, availableFeeCodes));
  }

  private void addMatterTypeChangeIfChanged(
      List<ClaimHistoryAmendmentChange> resolvedChanges,
      AreaOfLaw areaOfLaw,
      Map<String, String> availableFeeCodes,
      String[] beforeParts,
      String[] afterParts,
      int indexOfPart) {
    var beforePart = matterTypePart(beforeParts, indexOfPart);
    var afterPart = matterTypePart(afterParts, indexOfPart);
    if (Objects.equals(beforePart, afterPart)) {
      return;
    }

    var matterTypeField = resolveMatterTypeField(areaOfLaw, indexOfPart);
    if (matterTypeField.isEmpty()) {
      return;
    }

    var field = matterTypeField.get();
    resolvedChanges.add(
        new ClaimHistoryAmendmentChange(
            field,
            FIELD_IDENTIFIER_MATTER_TYPE_CODE,
            resolveValue(beforePart, field, availableFeeCodes),
            resolveValue(afterPart, field, availableFeeCodes),
            areaOfLaw));
  }

  private static String[] splitMatterTypeCode(Object value) {
    var raw = toFallbackString(value);
    if (raw == null) {
      return new String[0];
    }
    return raw.split("[+:]", -1);
  }

  private static String matterTypePart(String[] parts, int index) {
    return parts.length > index ? parts[index] : null;
  }

  private static Optional<ClaimViewField<?>> resolveMatterTypeField(
      AreaOfLaw areaOfLaw, int index) {
    return switch (areaOfLaw) {
      case LEGAL_HELP ->
          Optional.of(
              index == 0
                  ? CivilClaimDetailsViewField.MATTER_TYPE_CODE_1
                  : CivilClaimDetailsViewField.MATTER_TYPE_CODE_2);
      case MEDIATION ->
          Optional.of(
              index == 0
                  ? MediationClaimDetailsViewField.MATTER_TYPE_CODE_1
                  : MediationClaimDetailsViewField.MATTER_TYPE_CODE_2);
      default -> Optional.empty();
    };
  }

  private static Optional<ClaimViewField<?>> resolveField(
      AreaOfLaw areaOfLaw, String fieldIdentifier) {
    if (areaOfLaw == null || fieldIdentifier == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(AMENDABLE_FIELDS_BY_IDENTIFIER.get(areaOfLaw))
        .map(fields -> fields.get(fieldIdentifier));
  }

  private static Object resolveValue(
      Object raw, ClaimViewField<?> field, Map<String, String> availableFeeCodes) {
    if (field == ClaimDetailsViewField.FEE_CODE) {
      return resolveFeeCodeDisplay(raw, availableFeeCodes);
    }
    return resolveValue(raw, field);
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
      log.warn(
          "Using raw value as failed to resolve value '{}' as field type {}: {}",
          raw,
          fieldType,
          e.getMessage(),
          e);
    }
    return raw;
  }

  private static String resolveFeeCodeDisplay(Object raw, Map<String, String> availableFeeCodes) {
    var feeCode = toFallbackString(raw);
    if (feeCode == null) {
      return null;
    }
    return availableFeeCodes.getOrDefault(feeCode, feeCode);
  }

  private static Object resolveEnumValue(Object raw, List<FieldOption> options) {
    var rawString = String.valueOf(raw);
    return options.stream()
        .filter(option -> option.value().equals(rawString))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Unable to resolve value for option %s in options %s"
                        .formatted(rawString, options)));
  }

  private static String toFallbackString(Object rawValue) {
    return rawValue == null ? null : String.valueOf(rawValue);
  }

  private static Map<String, ClaimViewField<?>> viewFieldsByAreaOfLaw(AreaOfLaw areaOfLaw) {
    var lookup = new LinkedHashMap<String, ClaimViewField<?>>();
    areaOfLawViewFields(areaOfLaw)
        .forEach(field -> putFieldIdentifier(lookup, field.getClaimsApiFieldName(), field));
    return Map.copyOf(lookup);
  }

  private static Stream<ClaimViewField<?>> areaOfLawViewFields(AreaOfLaw areaOfLaw) {
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

  private static void putFieldIdentifier(
      Map<String, ClaimViewField<?>> fieldLookup, String identifier, ClaimViewField<?> field) {
    if (identifier == null || identifier.isBlank()) {
      return;
    }
    if (field.getAmendability() == NEVER) {
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
}
