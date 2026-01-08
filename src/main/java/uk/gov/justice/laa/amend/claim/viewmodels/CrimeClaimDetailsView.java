package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.util.List;
import java.util.Map;


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
    public void addMatterTypeRow(Map<String, Object> summaryRows) {
        summaryRows.put("legalMatterCode", claim.getMatterTypeCode());
    }

    @Override
    public List<ClaimField> claimFields() {
        List<ClaimField> fields = ClaimDetailsView.super.claimFields();
        addRowIfNotNull(
            fields,
            setDisplayForNulls(claim.getTravelCosts()),
            setDisplayForNulls(claim.getWaitingCosts())
        );
        return fields;
    }

    @Override
    public List<ClaimField> summaryClaimFields() {
        return claimFields();
    }

    @Override
    public List<ClaimField> reviewClaimFields() {
        return claimFields();
    }
}
