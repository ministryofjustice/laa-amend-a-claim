package uk.gov.justice.laa.payments.amend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryChangeEntry;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEvent;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryResultSet;

class ClaimHistoryMetadataMapperTest {

  @Test
  void toApiEventsMapsTypedMetadataByEventType() {
    var timestamp = OffsetDateTime.now();
    var sourceId = UUID.randomUUID();
    var history =
        new ClaimHistoryResultSet()
            .events(
                List.of(
                    new ClaimHistoryEvent()
                        .eventType(ClaimHistoryEventType.SUBMISSION)
                        .eventTimestamp(timestamp)
                        .sourceId(sourceId)
                        .metadata(
                            Map.of(
                                "submission_period",
                                "APR-2026",
                                "office_account_number",
                                "123456",
                                "area_of_law",
                                "LEGAL HELP")),
                    new ClaimHistoryEvent()
                        .eventType(ClaimHistoryEventType.AMENDMENT)
                        .eventTimestamp(timestamp)
                        .metadata(
                            Map.of(
                                "requested_by_code",
                                "PROVIDER",
                                "amendment_reason_code",
                                "CORRECTION",
                                "pricing_recalculated",
                                "true",
                                "price_changed",
                                true,
                                "escape_case_logged",
                                false,
                                "changes",
                                List.of(
                                    change("claim.feeCode", null, "NEWFEE", "FSP"),
                                    change("client.surname", "OLD", "NEW", "REQUESTED"),
                                    "not-a-map"))),
                    new ClaimHistoryEvent()
                        .eventType(ClaimHistoryEventType.ASSESSMENT)
                        .eventTimestamp(timestamp)
                        .metadata(
                            Map.of(
                                "assessment_type",
                                "ESCAPE_CASE_ASSESSMENT",
                                "assessment_outcome",
                                "PAID_IN_FULL")),
                    new ClaimHistoryEvent()
                        .eventType(ClaimHistoryEventType.VOID)
                        .eventTimestamp(timestamp)
                        .metadata(
                            Map.of("assessment_type", "VOID", "assessment_reason", "reason"))));

    var mappedEvents = ClaimHistoryMetadataMapper.toApiEvents(history);

    assertThat(mappedEvents).hasSize(4);
    assertThat(mappedEvents.get(0).submissionMetadata().getOfficeAccountNumber())
        .isEqualTo("123456");
    assertThat(mappedEvents.get(1).amendmentMetadata().getRequestedByCode()).isEqualTo("PROVIDER");
    assertThat(mappedEvents.get(1).amendmentMetadata().getAmendmentReasonCode())
        .isEqualTo("CORRECTION");
    assertThat(mappedEvents.get(1).amendmentMetadata().getPricingRecalculated()).isTrue();
    assertThat(mappedEvents.get(1).amendmentMetadata().getPriceChanged()).isTrue();
    assertThat(mappedEvents.get(1).amendmentMetadata().getEscapeCaseLogged()).isFalse();
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges()).hasSize(2);
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(0).getFieldIdentifier())
        .isEqualTo("claim.feeCode");
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(0).getBefore()).isNull();
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(0).getAfter())
        .isEqualTo("NEWFEE");
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(0).getChangeSource())
        .isEqualTo(ClaimHistoryChangeEntry.ChangeSourceEnum.FSP);
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(1).getFieldIdentifier())
        .isEqualTo("client.surname");
    assertThat(mappedEvents.get(1).amendmentMetadata().getChanges().get(1).getChangeSource())
        .isEqualTo(ClaimHistoryChangeEntry.ChangeSourceEnum.REQUESTED);
    assertThat(mappedEvents.get(2).assessmentMetadata().getAssessmentOutcome())
        .isEqualTo("PAID_IN_FULL");
    assertThat(mappedEvents.get(3).voidMetadata().getAssessmentType()).isEqualTo("VOID");
  }

  private static Map<String, Object> change(
      String fieldIdentifier, Object before, Object after, String changeSource) {
    var change = new LinkedHashMap<String, Object>();
    change.put("field_identifier", fieldIdentifier);
    change.put("before", before);
    change.put("after", after);
    change.put("change_source", changeSource);
    return change;
  }
}
