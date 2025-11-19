package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.util.List;
import java.util.Map;


public record CrimeClaimDetailsView(CrimeClaimDetails claim) implements ClaimDetailsView<CrimeClaimDetails> {

    @Override
    public void addClaimTypeSpecificRows(List<ClaimField> rows) {
        addRowIfNotNull(
            rows,
            claim.getTravelCosts(),
            claim.getWaitingCosts()
        );
    }

    @Override
    public void addUcnSummaryRow(Map<String, Object> summaryRows) {

    }

    @Override
    public void addMatterTypeField(Map<String, Object> summaryRows) {
        summaryRows.put("legalMatterCode", claim.getMatterTypeCode());
    }
}
