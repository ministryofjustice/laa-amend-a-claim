package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;
import uk.gov.justice.laa.payments.amend.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.payments.amend.models.enums.OutcomeType;

public record ClaimHistoryAssessedEvent(
    OffsetDateTime eventDateTime,
    String user,
    AssessmentTypeEnum assessmentType,
    OutcomeType outcomeType)
    implements BaseClaimHistoryEvent {}
