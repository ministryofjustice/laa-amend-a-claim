package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.Claim;

public record ClaimView(Claim claim) implements BaseClaimView<Claim> {}
