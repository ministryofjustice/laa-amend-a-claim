package uk.gov.justice.laa.payments.amend.models.history;

import jakarta.annotation.Nullable;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record ClaimHistoryAmendmentChange(
    @Nullable ClaimViewField<?> field,
    String fieldIdentifier,
    @Nullable Object before,
    @Nullable Object after,
    @Nullable AreaOfLaw areaOfLaw) {}
