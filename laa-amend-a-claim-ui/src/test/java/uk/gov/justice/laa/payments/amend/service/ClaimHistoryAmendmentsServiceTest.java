package uk.gov.justice.laa.payments.amend.service;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType.AMENDMENT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReferenceList;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MicrosoftApiUser;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.models.enums.GenderCode;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryAmendedEvent;
import uk.gov.justice.laa.payments.amend.models.history.ClaimHistoryFspEvent;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

@ExtendWith(MockitoExtension.class)
class ClaimHistoryAmendmentsServiceTest {

  @Mock private UserRetrievalService userRetrievalService;
  @Mock private SystemReferenceService systemReferenceService;
  @Mock private AvailableFeeCodesService availableFeeCodesService;

  private ClaimHistoryAmendmentsService claimHistoryAmendmentsService;

  @BeforeEach
  void setUp() {
    claimHistoryAmendmentsService =
        new ClaimHistoryAmendmentsService(
            userRetrievalService, systemReferenceService, availableFeeCodesService);
  }

  @Test
  void toAmendmentClaimHistoryEventsIncludesResolvedRequestedAmendmentChanges() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var amendedUser =
        new MicrosoftApiUser(UUID.randomUUID().toString(), "Amended user", null, null);
    when(userRetrievalService.getUser(amendedUser.id())).thenReturn(amendedUser);

    var referenceList = new AmendmentRequestedByReferenceList().requestedBy(List.of());
    when(systemReferenceService.getAmendmentRequestedByReferenceList()).thenReturn(referenceList);
    when(systemReferenceService.getAmendmentRequestedByOptions(referenceList))
        .thenReturn(Map.of("PROVIDER", "Provider"));
    when(systemReferenceService.getAmendmentRequestReason("PROVIDER", referenceList))
        .thenReturn(Map.of("CORRECTION", "Correction"));
    when(availableFeeCodesService.getAvailableFeeCodes(claim.getAreaOfLaw()))
        .thenReturn(Map.of("OLDFEE", "OLDFEE - Old fee", "NEWFEE", "NEWFEE - New fee"));

    var changes =
        List.of(
            change("REQUESTED", "client.genderCode", "M", "F"),
            change("REQUESTED", "claim.caseStartDate", "2026-04-01", "2026-04-02"),
            change("REQUESTED", "unknown.field", "before", "after"),
            change("FSP", "claim.feeCode", "OLDFEE", "NEWFEE"),
            change("FSP", "claim.caseConcludedDate", "2026-04-05", "2026-04-06"));

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .actorId(amendedUser.id())
                        .metadata(
                            Map.of(
                                "requested_by_code",
                                "PROVIDER",
                                "amendment_reason_code",
                                "CORRECTION",
                                "changes",
                                changes))));

    var events =
        claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(history, claim).toList();
    assertThat(events).hasSize(1);

    var amendmentEvent = (ClaimHistoryAmendedEvent) events.getFirst();
    assertThat(amendmentEvent.user()).isEqualTo(amendedUser.displayName());
    assertThat(amendmentEvent.requestedByCode()).isEqualTo("Provider");
    assertThat(amendmentEvent.amendmentReasonCode()).isEqualTo("Correction");
    assertThat(amendmentEvent.amendmentChanges()).hasSize(4);

    var genderChange =
        amendmentEvent.amendmentChanges().stream()
            .filter(change -> "client.genderCode".equals(change.fieldIdentifier()))
            .findFirst()
            .orElseThrow();
    assertThat(genderChange.field()).isNotNull();
    assertThat(genderChange.field().name()).isEqualTo("GENDER");
    assertThat(genderChange.before()).isEqualTo(GenderCode.MALE);
    assertThat(genderChange.after()).isEqualTo(GenderCode.FEMALE);

    var unknownFieldChange =
        amendmentEvent.amendmentChanges().stream()
            .filter(change -> "unknown.field".equals(change.fieldIdentifier()))
            .findFirst()
            .orElseThrow();
    assertThat(unknownFieldChange.field()).isNull();
    assertThat(unknownFieldChange.before()).isEqualTo("before");
    assertThat(unknownFieldChange.after()).isEqualTo("after");

    var feeCodeChange =
        amendmentEvent.amendmentChanges().stream()
            .filter(change -> "claim.feeCode".equals(change.fieldIdentifier()))
            .findFirst()
            .orElseThrow();
    assertThat(feeCodeChange.field()).isNotNull();
    assertThat(feeCodeChange.field().name()).isEqualTo("FEE_CODE");
    assertThat(feeCodeChange.before()).isEqualTo("OLDFEE - Old fee");
    assertThat(feeCodeChange.after()).isEqualTo("NEWFEE - New fee");
  }

  @Test
  void toAmendmentClaimHistoryEventsReturnsEmptyWhenHistoryOrEventsMissing() {
    var claim = MockClaimsFunctions.createMockCivilClaim();

    assertThat(claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(null, claim).toList())
        .isEmpty();

    var noEventsHistory = new ClaimHistoryResultSet().claimId(claim.getClaimId());
    assertThat(
            claimHistoryAmendmentsService
                .toAmendmentClaimHistoryEvents(noEventsHistory, claim)
                .toList())
        .isEmpty();
  }

  @Test
  void toAmendmentClaimHistoryEventsResolvesMatterTypeTwoWhenOnlySecondPartChanges() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "changes",
                                List.of(
                                    change(
                                        "REQUESTED",
                                        "claim.matterTypeCode",
                                        "ABC123:DEF456",
                                        "ABC123:XYZ999"))))));

    var events =
        claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(history, claim).toList();

    var amendmentEvent = (ClaimHistoryAmendedEvent) events.getFirst();
    assertThat(amendmentEvent.amendmentChanges()).hasSize(1);
    var change = amendmentEvent.amendmentChanges().getFirst();
    assertThat(change.field()).isEqualTo(CivilClaimDetailsViewField.MATTER_TYPE_CODE_2);
    assertThat(change.before()).isEqualTo("DEF456");
    assertThat(change.after()).isEqualTo("XYZ999");
  }

  @Test
  void toAmendmentClaimHistoryEventsResolvesMatterTypeOneAndTwoWhenBothPartsChange() {
    var claim = MockClaimsFunctions.createMockMediationClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "changes",
                                List.of(
                                    change(
                                        "REQUESTED",
                                        "claim.matterTypeCode",
                                        "AAA111:BBB222",
                                        "CCC333:DDD444"))))));

    var events =
        claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(history, claim).toList();

    var amendmentEvent = (ClaimHistoryAmendedEvent) events.getFirst();
    assertThat(amendmentEvent.amendmentChanges()).hasSize(2);
    assertThat(amendmentEvent.amendmentChanges().get(0).field())
        .isEqualTo(MediationClaimDetailsViewField.MATTER_TYPE_CODE_1);
    assertThat(amendmentEvent.amendmentChanges().get(0).before()).isEqualTo("AAA111");
    assertThat(amendmentEvent.amendmentChanges().get(0).after()).isEqualTo("CCC333");
    assertThat(amendmentEvent.amendmentChanges().get(1).field())
        .isEqualTo(MediationClaimDetailsViewField.MATTER_TYPE_CODE_2);
    assertThat(amendmentEvent.amendmentChanges().get(1).before()).isEqualTo("BBB222");
    assertThat(amendmentEvent.amendmentChanges().get(1).after()).isEqualTo("DDD444");
  }

  @ParameterizedTest(name = "{0} maps {1}")
  @MethodSource("allMappedFields")
  void toAmendmentClaimHistoryEventsResolvesAllMappedFields(
      AreaOfLaw areaOfLaw, ClaimViewField<?> expectedField) {
    var claim = claimForArea(areaOfLaw);
    var before = sampleValue(expectedField, false);
    var after = sampleValue(expectedField, true);
    if (expectedField == ClaimDetailsViewField.FEE_CODE) {
      when(availableFeeCodesService.getAvailableFeeCodes(areaOfLaw))
          .thenReturn(
              Map.of(
                  String.valueOf(before.raw()),
                  "Before fee",
                  String.valueOf(after.raw()),
                  "After fee"));
    }

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "changes",
                                List.of(
                                    change(
                                        "REQUESTED",
                                        expectedField.getClaimsApiFieldName(),
                                        before.raw(),
                                        after.raw()))))));

    var events =
        claimHistoryAmendmentsService.toAmendmentClaimHistoryEvents(history, claim).toList();
    assertThat(events).hasSize(1);

    var amendmentEvent = (ClaimHistoryAmendedEvent) events.getFirst();
    assertThat(amendmentEvent.amendmentChanges()).hasSize(1);

    var actual = amendmentEvent.amendmentChanges().getFirst();
    assertThat(actual.areaOfLaw()).isEqualTo(areaOfLaw);
    assertThat(actual.fieldIdentifier()).isEqualTo(expectedField.getClaimsApiFieldName());
    assertThat(actual.field()).isEqualTo(expectedField);
    if (expectedField == ClaimDetailsViewField.FEE_CODE) {
      assertThat(actual.before()).isEqualTo("Before fee");
      assertThat(actual.after()).isEqualTo("After fee");
    } else {
      assertThat(actual.before()).isEqualTo(before.expected());
      assertThat(actual.after()).isEqualTo(after.expected());
    }
  }

  private static Stream<Arguments> allMappedFields() {
    return Stream.of(AreaOfLaw.CRIME_LOWER, AreaOfLaw.LEGAL_HELP, AreaOfLaw.MEDIATION)
        .flatMap(
            areaOfLaw ->
                mappedFieldsForArea(areaOfLaw).stream()
                    .map(field -> Arguments.of(areaOfLaw, field)));
  }

  @ParameterizedTest(name = "{0} resolves FSP {1}")
  @MethodSource("allMappedFspFields")
  void toFspClaimHistoryEventsResolvesAllMappedFspFields(
      AreaOfLaw areaOfLaw, ClaimViewField<?> expectedField) {
    var claim = claimForArea(areaOfLaw);
    var before = sampleValue(expectedField, false);
    var after = sampleValue(expectedField, true);
    if (expectedField == ClaimDetailsViewField.FEE_CODE) {
      when(availableFeeCodesService.getAvailableFeeCodes(areaOfLaw))
          .thenReturn(
              Map.of(
                  String.valueOf(before.raw()),
                  "Before fee",
                  String.valueOf(after.raw()),
                  "After fee"));
    }

    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "price_changed",
                                true,
                                "changes",
                                List.of(
                                    change("FSP", "fee.totalAmount", "100.00", "200.00"),
                                    change(
                                        "FSP",
                                        expectedField.getFeeApiFieldName(),
                                        before.raw(),
                                        after.raw()))))));

    var events =
        claimHistoryAmendmentsService
            .toFspClaimHistoryEventsFromApiEvents(
                ClaimHistoryMetadataMapper.toApiEvents(history), claim)
            .toList();
    assertThat(events).hasSize(1);

    var fspEvent = (ClaimHistoryFspEvent) events.getFirst();
    assertThat(fspEvent.recalculatedChanges()).hasSize(1);

    var actual = fspEvent.recalculatedChanges().getFirst();
    assertThat(actual.areaOfLaw()).isEqualTo(areaOfLaw);
    assertThat(actual.fieldIdentifier()).isEqualTo(expectedField.getFeeApiFieldName());
    assertThat(actual.field()).isEqualTo(expectedField);
    if (expectedField == ClaimDetailsViewField.FEE_CODE) {
      assertThat(actual.before()).isEqualTo("Before fee");
      assertThat(actual.after()).isEqualTo("After fee");
    } else {
      assertThat(actual.before()).isEqualTo(before.expected());
      assertThat(actual.after()).isEqualTo(after.expected());
    }
  }

  private static Stream<Arguments> allMappedFspFields() {
    return Stream.of(AreaOfLaw.CRIME_LOWER, AreaOfLaw.LEGAL_HELP, AreaOfLaw.MEDIATION)
        .flatMap(
            areaOfLaw ->
                mappedFspFieldsForArea(areaOfLaw).stream()
                    .map(field -> Arguments.of(areaOfLaw, field)));
  }

  @ParameterizedTest(name = "ignores FSP field {0}")
  @MethodSource("ignoredFspFieldIdentifiers")
  void toFspClaimHistoryEventsIgnoresConfiguredFields(String fspFieldIdentifier) {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "price_changed",
                                true,
                                "changes",
                                List.of(
                                    change("FSP", "fee.totalAmount", "100.00", "200.00"),
                                    change("FSP", fspFieldIdentifier, "1", "2"))))));

    var events =
        claimHistoryAmendmentsService
            .toFspClaimHistoryEventsFromApiEvents(
                ClaimHistoryMetadataMapper.toApiEvents(history), claim)
            .toList();

    assertThat(events).hasSize(1);
    var fspEvent = (ClaimHistoryFspEvent) events.getFirst();
    assertThat(fspEvent.recalculatedChanges()).isEmpty();
  }

  private static Stream<Arguments> ignoredFspFieldIdentifiers() {
    return Stream.of(
            "fee.boltOnAdjournedHearingCount",
            "fee.boltOnCmrhTelephoneCount",
            "fee.boltOnCmrhOralCount",
            "fee.boltOnHomeOfficeInterviewCount",
            "fee.feeCodeDescription",
            "fee.feeCode",
            "fee.vatIndicator",
            "fee.requestedNetProfitCostsAmount",
            "fee.requestedNetDisbursementAmount")
        .map(Arguments::of);
  }

  private static List<ClaimViewField<?>> mappedFieldsForArea(AreaOfLaw areaOfLaw) {
    var fields =
        new LinkedHashSet<ClaimViewField<?>>(
            stream(ClaimDetailsViewField.values())
                .filter(ClaimHistoryAmendmentsServiceTest::hasClaimsApiIdentifier)
                .toList());

    switch (areaOfLaw) {
      case CRIME_LOWER ->
          fields.addAll(
              stream(CrimeClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasClaimsApiIdentifier)
                  .toList());
      case LEGAL_HELP ->
          fields.addAll(
              stream(CivilClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasClaimsApiIdentifier)
                  .toList());
      case MEDIATION ->
          fields.addAll(
              stream(MediationClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasClaimsApiIdentifier)
                  .toList());
      default -> throw new IllegalArgumentException("Unexpected area of law: " + areaOfLaw);
    }

    var byIdentifier = new LinkedHashMap<String, ClaimViewField<?>>();
    fields.forEach(field -> byIdentifier.putIfAbsent(field.getClaimsApiFieldName(), field));
    return List.copyOf(byIdentifier.values());
  }

  private static List<ClaimViewField<?>> mappedFspFieldsForArea(AreaOfLaw areaOfLaw) {
    var fields = new LinkedHashSet<ClaimViewField<?>>();
    fields.addAll(
        stream(ClaimDetailsViewField.values())
            .filter(ClaimHistoryAmendmentsServiceTest::hasFeeApiIdentifier)
            .toList());

    switch (areaOfLaw) {
      case CRIME_LOWER ->
          fields.addAll(
              stream(CrimeClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasFeeApiIdentifier)
                  .toList());
      case LEGAL_HELP ->
          fields.addAll(
              stream(CivilClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasFeeApiIdentifier)
                  .toList());
      case MEDIATION ->
          fields.addAll(
              stream(MediationClaimDetailsViewField.values())
                  .filter(ClaimHistoryAmendmentsServiceTest::hasFeeApiIdentifier)
                  .toList());
      default -> throw new IllegalArgumentException("Unexpected area of law: " + areaOfLaw);
    }

    var byIdentifier = new LinkedHashMap<String, ClaimViewField<?>>();
    fields.stream()
        .filter(field -> !isIgnoredFspFieldIdentifier(field.getFeeApiFieldName()))
        .forEach(field -> byIdentifier.putIfAbsent(field.getFeeApiFieldName(), field));
    return List.copyOf(byIdentifier.values());
  }

  private static final Set<String> IGNORED_FSP_FIELDS =
      Set.of(
          "boltOnAdjournedHearingCount",
          "boltOnCmrhTelephoneCount",
          "boltOnCmrhOralCount",
          "boltOnHomeOfficeInterviewCount",
          "feeCodeDescription",
          "feeCode",
          "vatIndicator",
          "requestedNetProfitCostsAmount",
          "requestedNetDisbursementAmount");

  private static boolean isIgnoredFspFieldIdentifier(String fieldIdentifier) {
    if (fieldIdentifier == null || fieldIdentifier.isBlank()) {
      return false;
    }
    if (IGNORED_FSP_FIELDS.contains(fieldIdentifier)) {
      return true;
    }
    return fieldIdentifier.startsWith("fee.")
        && IGNORED_FSP_FIELDS.contains(fieldIdentifier.substring("fee.".length()));
  }

  private static boolean hasClaimsApiIdentifier(ClaimViewField<?> field) {
    var identifier = field.getClaimsApiFieldName();
    return identifier != null
        && !identifier.isBlank()
        && field.getAmendability()
            != uk.gov.justice.laa.payments.amend.models.enums.Amendability.NEVER;
  }

  private static boolean hasFeeApiIdentifier(ClaimViewField<?> field) {
    var identifier = field.getFeeApiFieldName();
    return identifier != null && !identifier.isBlank();
  }

  private static ClaimDetails claimForArea(AreaOfLaw areaOfLaw) {
    return switch (areaOfLaw) {
      case CRIME_LOWER -> MockClaimsFunctions.createMockCrimeClaim();
      case LEGAL_HELP -> MockClaimsFunctions.createMockCivilClaim();
      case MEDIATION -> MockClaimsFunctions.createMockMediationClaim();
    };
  }

  private static SampleValue sampleValue(ClaimViewField<?> field, boolean isAfter) {
    return switch (field.getFieldType()) {
      case TEXT -> {
        var raw = isAfter ? "after" : "before";
        yield new SampleValue(raw, raw);
      }
      case BOOLEAN -> {
        var raw = isAfter ? "false" : "true";
        yield new SampleValue(raw, Boolean.parseBoolean(raw));
      }
      case NUMBER -> {
        var raw = isAfter ? "8" : "7";
        yield new SampleValue(raw, Integer.parseInt(raw));
      }
      case BIG_DECIMAL -> {
        var raw = isAfter ? "201.75" : "100.50";
        yield new SampleValue(raw, new BigDecimal(raw));
      }
      case DATE -> {
        var raw = isAfter ? "2026-04-02" : "2026-04-01";
        yield new SampleValue(raw, LocalDate.parse(raw));
      }
      case ENUM -> {
        var options = field.getOptions();
        var option = options.get(isAfter && options.size() > 1 ? 1 : 0);
        yield new SampleValue(option.value(), option);
      }
    };
  }

  private record SampleValue(Object raw, Object expected) {}

  private static LinkedHashMap<String, Object> change(
      String source, String fieldIdentifier, Object before, Object after) {
    var change = new LinkedHashMap<String, Object>();
    change.put("change_source", source);
    change.put("field_identifier", fieldIdentifier);
    change.put("before", before);
    change.put("after", after);
    return change;
  }

  @Test
  void toFspClaimHistoryEventsReturnsEmptyWhenNoFspChanges() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "changes",
                                List.of(change("REQUESTED", "client.genderCode", "M", "F"))))));

    var events =
        claimHistoryAmendmentsService.toFspClaimHistoryEventsFromApiEvents(
            ClaimHistoryMetadataMapper.toApiEvents(history), claim);

    assertThat(events.toList()).isEmpty();
  }

  @Test
  void toFspClaimHistoryEventsReturnsFspEventWithTotalAmounts() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var eventTime = java.time.OffsetDateTime.of(2026, 5, 1, 10, 0, 0, 0, java.time.ZoneOffset.UTC);
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .eventTimestamp(eventTime)
                        .metadata(
                            Map.of(
                                "price_changed",
                                true,
                                "changes",
                                List.of(
                                    change("FSP", "fee.totalAmount", "1000.00", "1200.50"),
                                    change(
                                        "FSP", "fee.netProfitCostsAmount", "500.00", "600.00"))))));

    var events =
        claimHistoryAmendmentsService
            .toFspClaimHistoryEventsFromApiEvents(
                ClaimHistoryMetadataMapper.toApiEvents(history), claim)
            .toList();

    assertThat(events).hasSize(1);
    var fspEvent = (ClaimHistoryFspEvent) events.getFirst();
    assertThat(fspEvent.eventDateTime()).isEqualTo(eventTime);
    assertThat(fspEvent.totalBefore()).isEqualByComparingTo(new BigDecimal("1000.00"));
    assertThat(fspEvent.totalAfter()).isEqualByComparingTo(new BigDecimal("1200.50"));
    assertThat(fspEvent.recalculatedChanges()).hasSize(1);
    assertThat(fspEvent.recalculatedChanges().getFirst().fieldIdentifier())
        .isEqualTo("fee.netProfitCostsAmount");
  }

  @Test
  void toFspClaimHistoryEventsExcludesTotalAmountFromRecalculatedFields() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "price_changed",
                                true,
                                "changes",
                                List.of(change("FSP", "fee.totalAmount", "1000.00", "1200.50"))))));

    var events =
        claimHistoryAmendmentsService
            .toFspClaimHistoryEventsFromApiEvents(
                ClaimHistoryMetadataMapper.toApiEvents(history), claim)
            .toList();

    assertThat(events).hasSize(1);
    var fspEvent = (ClaimHistoryFspEvent) events.getFirst();
    assertThat(fspEvent.recalculatedChanges()).isEmpty();
  }

  @Test
  void toFspClaimHistoryEventsDoesNotThrowWhenPriceChangedButTotalAmountMissing() {
    var claim = MockClaimsFunctions.createMockCivilClaim();
    var history =
        new ClaimHistoryResultSet()
            .claimId(claim.getClaimId())
            .events(
                List.of(
                    new uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent()
                        .eventType(AMENDMENT)
                        .metadata(
                            Map.of(
                                "price_changed",
                                true,
                                "changes",
                                List.of(
                                    change(
                                        "FSP", "fee.netProfitCostsAmount", "500.00", "600.00"))))));

    var events =
        claimHistoryAmendmentsService
            .toFspClaimHistoryEventsFromApiEvents(
                ClaimHistoryMetadataMapper.toApiEvents(history), claim)
            .toList();

    assertThat(events).hasSize(1);
    var fspEvent = (ClaimHistoryFspEvent) events.getFirst();
    assertThat(fspEvent.totalBefore()).isNull();
    assertThat(fspEvent.totalAfter()).isNull();
    assertThat(fspEvent.recalculatedChanges()).hasSize(1);
    assertThat(fspEvent.recalculatedChanges().getFirst().fieldIdentifier())
        .isEqualTo("fee.netProfitCostsAmount");
  }
}
