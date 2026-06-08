package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;

public record CivilClaimCostsView(List<ClaimFieldRow> rows, boolean hasAssessment)
    implements ClaimCostsView {

  public CivilClaimCostsView(CivilClaimDetails claim) {
    this(createRows(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static List<ClaimFieldRow> createRows(CivilClaimDetails claim) {
    return Stream.of(
            claim.getFixedFee(),
            claim.getNetProfitCost(),
            claim.getNetDisbursementAmount(),
            claim.getNetCounselCosts(),
            claim.getDisbursementVatAmount(),
            claim.getTravelAndWaitingCosts(),
            claim.getIsVatApplicable(),
            claim.getAdjournedHearing(),
            claim.getDetentionTravelWaitingCosts(),
            claim.getJrFormFillingCost(),
            claim.getSubstantiveHearing(),
            claim.getHoInterview(),
            claim.getCmrhOral(),
            claim.getCmrhTelephone(),
            claim.getIsLondonRate(),
            claim.getPriorAuthorityReference())
        .map(ClaimField::toClaimFieldRow)
        .toList();
  }
}
