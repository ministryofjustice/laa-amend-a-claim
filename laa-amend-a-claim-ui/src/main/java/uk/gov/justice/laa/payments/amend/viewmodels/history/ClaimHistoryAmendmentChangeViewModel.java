package uk.gov.justice.laa.payments.amend.viewmodels.history;

import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafString;

public record ClaimHistoryAmendmentChangeViewModel(
    ThymeleafString fieldLabel, Object before, Object after) {}
