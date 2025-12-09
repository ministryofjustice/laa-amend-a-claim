package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimDetails extends ClaimDetails {

    private ClaimField travelCosts;
    private ClaimField waitingCosts;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        applyIfNotNull(travelCosts, ClaimField::setNilled);
        applyIfNotNull(waitingCosts, ClaimField::setNilled);
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(travelCosts, ClaimField::setAmendedToCalculated);
        applyIfNotNull(waitingCosts, ClaimField::setAmendedToCalculated);
    }

    @Override
    public void setReducedValues() {
        super.setReducedValues();
        setPaidInFullOrReduced();
    }
      
    @Override
    public void setPaidInFullValues() {
        super.setPaidInFullValues();
        setPaidInFullOrReduced();
    }

    private void setPaidInFullOrReduced() {
        ClaimField assessedTotalVat = getAssessedTotalVat();
        ClaimField assessedTotalInclVat = getAssessedTotalInclVat();

        applyIfNotNull(travelCosts, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(waitingCosts, ClaimField::setAmendedToSubmitted);

        List<String> feeCodes = List.of("INVC");
        String feeCode = getFeeCode();

        // assessed total fields are only shown on crime claims if the claim has a certain fee code (e.g. INVC)
        if (feeCode != null && feeCodes.contains(feeCode)) {
            applyIfNotNull(assessedTotalVat, ClaimField::setToNeedsAmending);
            applyIfNotNull(assessedTotalInclVat, ClaimField::setToNeedsAmending);
        } else {
            applyIfNotNull(assessedTotalVat, ClaimField::setToDoNotDisplay);
            applyIfNotNull(assessedTotalInclVat, ClaimField::setToDoNotDisplay);
        }
    }

    @Override
    public boolean getIsCrimeClaim() {
        return true;
    }

    @Override
    public ClaimDetailsView<? extends Claim> toViewModel() {
        return new CrimeClaimDetailsView(this);
    }

    @Override
    public AssessmentPost toAssessment(AssessmentMapper mapper, String userId) {
        return mapper.mapCrimeClaimToAssessment(this, userId);
    }
}
