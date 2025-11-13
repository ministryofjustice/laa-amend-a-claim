package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimSummary extends ClaimSummary {
    private String matterTypeCode;
    private ClaimFieldRow travelCosts;
    private ClaimFieldRow waitingCosts;
}
