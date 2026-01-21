package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public record CrimeClaimDetailsView(CrimeClaimDetails claim) implements ClaimDetailsView<CrimeClaimDetails> {

    @Override
    public void addUcnSummaryRow(Map<String, Object> summaryRows) {}

    @Override
    public void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows) {
        summaryRows.put("policeStationCourtPrisonId", claim.getPoliceStationCourtPrisonId());
    }

    @Override
    public void addSchemeIdRow(Map<String, Object> summaryRows) {
        summaryRows.put("schemeId", claim.getSchemeId());
    }

    @Override
    public void addMatterTypeCodeRow(Map<String, Object> summaryRows) {
        summaryRows.put("legalMatterCode", claim.getMatterTypeCode());
    }

    @Override
    public Stream<ClaimField> claimFields() {
        return Stream.concat(
                ClaimDetailsView.super.claimFields(),
                Stream.of(
                    claim.getTravelCosts(),
                    claim.getWaitingCosts()
                )
            );
    }
}
