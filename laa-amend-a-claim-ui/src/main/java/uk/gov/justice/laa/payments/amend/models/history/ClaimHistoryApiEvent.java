package uk.gov.justice.laa.payments.amend.models.history;

import java.time.OffsetDateTime;
import java.util.UUID;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAmendmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryAssessmentMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryEventType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistorySubmissionMetadata;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimHistoryVoidMetadata;

public record ClaimHistoryApiEvent(
    ClaimHistoryEventType eventType,
    OffsetDateTime eventTimestamp,
    String actorId,
    UUID sourceId,
    ClaimHistorySubmissionMetadata submissionMetadata,
    ClaimHistoryAmendmentMetadata amendmentMetadata,
    ClaimHistoryAssessmentMetadata assessmentMetadata,
    ClaimHistoryVoidMetadata voidMetadata) {}
