package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
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
    public void addMatterTypeCodeRow(Map<String, Object> summaryRows) {
        summaryRows.put("legalMatterCode", claim.getMatterTypeCode());
    }

    @Override
    public List<ClaimField> claimFields() {
        List<ClaimField> fields = ClaimDetailsView.super.claimFields();
        addRowIfNotNull(
            fields,
            setChangeUrl(setDisplayForNulls(claim.getTravelCosts()), Cost.TRAVEL_COSTS),
            setChangeUrl(setDisplayForNulls(claim.getWaitingCosts()), Cost.WAITING_COSTS)
        );
        return fields;
    }

    @Override
    public List<ClaimField> claimFieldsWithBoltOns() {
        return claimFields();
    }
}
