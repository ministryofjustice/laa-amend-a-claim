package uk.gov.justice.laa.amend.claim.viewmodels;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.util.List;
import java.util.Map;


public record CivilClaimDetailsView(CivilClaimDetails claim) implements ClaimDetailsView<CivilClaimDetails> {

    @Override
    public void addUcnSummaryRow(Map<String, Object> summaryRows) {
        summaryRows.put("ucn", claim.getCaseReferenceNumber());
    }

    @Override
    public void addMatterTypeField(Map<String, Object> summaryRows) {
        summaryRows.put("matterTypeCodeOne", getMatterTypeCodeOne());
        summaryRows.put("matterTypeCodeTwo", getMatterTypeCodeTwo());
    }


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
