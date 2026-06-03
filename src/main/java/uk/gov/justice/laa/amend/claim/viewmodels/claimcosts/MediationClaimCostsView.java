package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public record MediationClaimCostsView(List<ClaimFieldRow> rows, boolean hasAssessment)
    implements ClaimCostsView {

  public MediationClaimCostsView(MediationClaimDetails claim) {
    this(createRows(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static List<ClaimFieldRow> createRows(MediationClaimDetails claim) {
    return Stream.of(
            claim.getFixedFee(),
            claim.getVatClaimed(),
            claim.getNetDisbursementAmount(),
            claim.getDisbursementVatAmount())
        .map(ClaimField::toClaimFieldRow)
        .toList();
  }
}
