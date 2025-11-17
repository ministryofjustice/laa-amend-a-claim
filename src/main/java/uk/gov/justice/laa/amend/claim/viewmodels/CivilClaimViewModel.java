package uk.gov.justice.laa.amend.claim.viewmodels;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.amend.claim.models.CivilClaim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

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
