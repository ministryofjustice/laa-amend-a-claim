package uk.gov.justice.laa.payments.amend.viewmodels;

import java.util.List;
import uk.gov.justice.laa.payments.amend.models.Claim;

public record SearchResultView(List<BaseClaimView<Claim>> claims, Pagination pagination) {}
