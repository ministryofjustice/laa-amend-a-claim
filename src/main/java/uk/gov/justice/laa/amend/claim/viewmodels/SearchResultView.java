package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.List;
import uk.gov.justice.laa.amend.claim.models.Claim;

public record SearchResultView(List<BaseClaimView<Claim>> claims, Pagination pagination) {}
