package uk.gov.justice.laa.payments.amend.viewmodels.history;

import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record ClaimHistoryAmendmentChangeViewModel(
    ClaimViewField<?> field, ThymeleafString fieldLabel, Object before, Object after) {}
