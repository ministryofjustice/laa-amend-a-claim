package uk.gov.justice.laa.payments.amend.viewmodels;

import uk.gov.justice.laa.payments.amend.models.Claim;

public record ClaimView(Claim claim) implements BaseClaimView<Claim> {}
