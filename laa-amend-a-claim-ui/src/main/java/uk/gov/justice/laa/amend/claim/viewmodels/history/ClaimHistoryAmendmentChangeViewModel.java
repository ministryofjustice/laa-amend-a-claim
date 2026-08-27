package uk.gov.justice.laa.amend.claim.viewmodels.history;

import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafString;

public record ClaimHistoryAmendmentChangeViewModel(
    ThymeleafString fieldLabel, Object before, Object after) {}
