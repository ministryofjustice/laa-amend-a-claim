package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CivilClaimSummary extends ClaimSummary {
    private String matterTypeCodeOne;
    private String matterTypeCodeTwo;
    private ClaimFieldRow detentionTravelWaitingCosts;
    private ClaimFieldRow jrFormFillingCost;
    private ClaimFieldRow adjournedHearing;
    private ClaimFieldRow cmrhTelephone;
    private ClaimFieldRow cmrhOral;
    private ClaimFieldRow hoInterview;
    private ClaimFieldRow substantiveHearing;
    private ClaimFieldRow counselsCost;
}
