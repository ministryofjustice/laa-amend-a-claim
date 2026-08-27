package uk.gov.justice.laa.amend.claim.models.history;

import java.util.List;
import java.util.Optional;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;

public record ClaimHistory(
    List<BaseClaimHistoryEvent> events, Optional<MicrosoftApiUser> latestAssessmentUser) {}
