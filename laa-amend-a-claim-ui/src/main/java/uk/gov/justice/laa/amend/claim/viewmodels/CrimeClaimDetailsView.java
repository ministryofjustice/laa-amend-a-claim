package uk.gov.justice.laa.amend.claim.viewmodels;

import java.util.Map;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

public record CrimeClaimDetailsView(CrimeClaimDetails claim)
    implements ClaimDetailsView<CrimeClaimDetails> {

  @Override
  public void addUcnSummaryRow(Map<String, Object> summaryRows) {}

  @Override
  public void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows) {
    summaryRows.put("POLICE_STATION_COURT_PRISON_ID", claim.getPoliceStationCourtPrisonId());
  }

  @Override
  public void addSchemeIdRow(Map<String, Object> summaryRows) {
    summaryRows.put("SCHEME_ID", claim.getSchemeId());
  }

  @Override
  public void addMatterTypeCodeRow(Map<String, Object> summaryRows) {
    summaryRows.put("MATTER_TYPE_CODE", claim.getMatterTypeCode());
  }

  @Override
  public Stream<ClaimField> claimFields() {
    return Stream.concat(
        ClaimDetailsView.super.claimFields(),
        Stream.of(claim.getTravelCosts(), claim.getWaitingCosts()));
  }
}
