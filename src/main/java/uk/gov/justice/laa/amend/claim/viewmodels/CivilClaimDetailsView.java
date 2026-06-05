package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.Map;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

public record CivilClaimDetailsView(CivilClaimDetails claim)
    implements ClaimDetailsView<CivilClaimDetails> {

  @Override
  public void addUcnSummaryRow(Map<String, Object> summaryRows) {
    summaryRows.put("ucn", claim.getUniqueClientNumber());
  }

  @Override
  public void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows) {}

  @Override
  public void addSchemeIdRow(Map<String, Object> summaryRows) {}

  @Override
  public void addMatterTypeCodeRow(Map<String, Object> summaryRows) {
    summaryRows.put("matterTypeCodeOne", claim.getMatterType1());
    summaryRows.put("matterTypeCodeTwo", claim.getMatterType2());
  }

  @Override
  public Stream<ClaimField> claimFields() {
    return Stream.concat(
        ClaimDetailsView.super.claimFields(),
        Stream.of(
            claim.getDetentionTravelWaitingCosts(),
            claim.getJrFormFillingCost(),
            claim.getCounselsCost(),
            claim.getCmrhOral(),
            claim.getCmrhTelephone(),
            claim.getHoInterview(),
            claim.getSubstantiveHearing(),
            claim.getAdjournedHearing()));
  }
}
