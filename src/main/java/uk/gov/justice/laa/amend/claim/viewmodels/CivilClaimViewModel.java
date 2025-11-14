package uk.gov.justice.laa.amend.claim.viewmodels;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.util.List;

public record CivilClaimViewModel(CivilClaim claim) implements ClaimViewModel<CivilClaim> {

    @Override
    public void addClaimTypeSpecificRows(List<ClaimField> rows) {
        addRowIfNotNull(
            rows,
            claim.getCounselsCost(),
            claim.getDetentionTravelWaitingCosts(),
            claim.getJrFormFillingCost(),
            claim.getAdjournedHearing(),
            claim.getCmrhTelephone(),
            claim.getCmrhOral(),
            claim.getHoInterview(),
            claim.getSubstantiveHearing()
        );
    }

    @Override
    public Cost getCostForRow(ClaimField row) {
        if (row == null) {
            return null;
        }

        if (row.equals(claim.getCounselsCost())) {
            return Cost.COUNSEL_COSTS;
        }
        if (row.equals(claim.getDetentionTravelWaitingCosts())) {
            return Cost.DETENTION_TRAVEL_AND_WAITING_COSTS;
        }
        if (row.equals(claim.getJrFormFillingCost())) {
            return Cost.JR_FORM_FILLING_COSTS;
        }

        // Fall back to parent implementation
        return ClaimViewModel.super.getCostForRow(row);
    }

    public String getMatterTypeCodeOne() {
        if (StringUtils.isNotEmpty(claim.getMatterTypeCode())) {
            String[] matterType = claim.getMatterTypeCode().split("[+:]");
            return matterType.length > 0 ? matterType[0] : null;
        }
        return null;
    }

    public String getMatterTypeCodeTwo() {
        if (StringUtils.isNotEmpty(claim.getMatterTypeCode())) {
            String[] matterType = claim.getMatterTypeCode().split("[+:]");
            return matterType.length > 1 ? matterType[1] : null;
        }
        return null;
    }
}
