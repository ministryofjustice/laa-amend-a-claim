package uk.gov.justice.laa.amend.claim.viewmodels;

import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.List;

public record CivilClaimViewModel(CivilClaim claim) implements ClaimViewModel<CivilClaim> {

    @Override
    public void addClaimTypeSpecificRows(List<ClaimField> rows) {
        if (claim.getCounselsCost() != null) rows.add(claim.getCounselsCost());
        if (claim.getDetentionTravelWaitingCosts() != null) rows.add(claim.getDetentionTravelWaitingCosts());
        if (claim.getJrFormFillingCost() != null) rows.add(claim.getJrFormFillingCost());
        if (claim.getAdjournedHearing() != null) rows.add(claim.getAdjournedHearing());
        if (claim.getCmrhTelephone() != null) rows.add(claim.getCmrhTelephone());
        if (claim.getCmrhOral() != null) rows.add(claim.getCmrhOral());
        if (claim.getHoInterview() != null) rows.add(claim.getHoInterview());
        if (claim.getSubstantiveHearing() != null) rows.add(claim.getSubstantiveHearing());
    }

    @Override
    public Cost getCostForRow(ClaimField row) {
        if (row == null) return null;

        if (row.equals(claim.getCounselsCost())) return Cost.COUNSEL_COSTS;
        if (row.equals(claim.getDetentionTravelWaitingCosts())) return Cost.DETENTION_TRAVEL_AND_WAITING_COSTS;
        if (row.equals(claim.getJrFormFillingCost())) return Cost.JR_FORM_FILLING_COSTS;

        // Fall back to parent implementation
        return ClaimViewModel.super.getCostForRow(row);
    }
}
