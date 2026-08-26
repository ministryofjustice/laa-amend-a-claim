package uk.gov.justice.laa.payments.amend.models;

import java.util.List;
import java.util.Optional;

public record ClaimHistory(
    List<ClaimHistoryEvent> events, Optional<MicrosoftApiUser> latestAssessmentUser) {}
