package uk.gov.justice.laa.amend.claim.viewmodels;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


public record CivilClaimDetailsView(CivilClaimDetails claim) implements ClaimDetailsView<CivilClaimDetails> {

    @Override
    public void addUcnSummaryRow(Map<String, Object> summaryRows) {
        summaryRows.put("ucn", claim.getCaseReferenceNumber());
    }

    @Override
    public void addPoliceStationCourtPrisonIdRow(Map<String, Object> summaryRows) {}

    @Override
    public void addSchemeIdRow(Map<String, Object> summaryRows) {}

    @Override
    public void addMatterTypeCodeRow(Map<String, Object> summaryRows) {
        summaryRows.put("matterTypeCodeOne", getMatterTypeCodeOne());
        summaryRows.put("matterTypeCodeTwo", getMatterTypeCodeTwo());
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
                    claim.getAdjournedHearing()
                )
            );
    }
}
