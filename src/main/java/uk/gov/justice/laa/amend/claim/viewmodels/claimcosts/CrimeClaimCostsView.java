package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public record CrimeClaimCostsView(List<ClaimFieldRow> rows, boolean hasAssessment)
    implements ClaimCostsView {

  public CrimeClaimCostsView(CrimeClaimDetails claim) {
    this(createRows(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static List<ClaimFieldRow> createRows(CrimeClaimDetails claim) {
    return Stream.of(
            claim.getFixedFee(),
            claim.getNetProfitCost(),
            claim.getNetDisbursementAmount(),
            claim.getTravelCosts(),
            claim.getWaitingCosts(),
            claim.getVatClaimed(),
            claim.getDisbursementVatAmount())
        .map(ClaimField::toClaimFieldRow)
        .toList();
  }
}
