package uk.gov.justice.laa.amend.claim.models.history;

import java.time.OffsetDateTime;
import uk.gov.justice.laa.amend.claim.models.enums.AssessmentTypeEnum;
import uk.gov.justice.laa.amend.claim.models.enums.OutcomeType;

public record ClaimHistoryAssessedEvent(
    OffsetDateTime eventDateTime,
    String user,
    AssessmentTypeEnum assessmentType,
    OutcomeType outcomeType)
    implements BaseClaimHistoryEvent {}
