package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@EqualsAndHashCode(callSuper = true)
@Data
public class CivilClaimDetails extends ClaimDetails {

    private ClaimField detentionTravelWaitingCosts;
    private ClaimField jrFormFillingCost;
    private ClaimField adjournedHearing;
    private ClaimField cmrhTelephone;
    private ClaimField cmrhOral;
    private ClaimField hoInterview;
    private ClaimField substantiveHearing;
    private ClaimField counselsCost;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        applyIfNotNull(counselsCost, ClaimField::setNilled);
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setNilled);
        applyIfNotNull(jrFormFillingCost, ClaimField::setNilled);
        applyIfNotNull(adjournedHearing, ClaimField::setNilled);
        applyIfNotNull(cmrhTelephone, ClaimField::setNilled);
        applyIfNotNull(cmrhOral, ClaimField::setNilled);
        applyIfNotNull(hoInterview, ClaimField::setNilled);
        applyIfNotNull(substantiveHearing, ClaimField::setNilled);
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAmendedToCalculated);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAmendedToCalculated);
        applyIfNotNull(adjournedHearing, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhTelephone, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhOral, ClaimField::setAmendedToCalculated);
        applyIfNotNull(hoInterview, ClaimField::setAmendedToCalculated);
        applyIfNotNull(substantiveHearing, ClaimField::setAmendedToCalculated);
        applyIfNotNull(counselsCost, ClaimField::setAmendedToCalculated);
    }

    @Override
    public void setReducedValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(counselsCost, ClaimField::setAmendedToSubmitted);

        // Bolt-ons get set to 0
        applyIfNotNull(adjournedHearing, ClaimField::setNilled);
        applyIfNotNull(cmrhTelephone, ClaimField::setNilled);
        applyIfNotNull(cmrhOral, ClaimField::setNilled);
        applyIfNotNull(hoInterview, ClaimField::setNilled);
        applyIfNotNull(substantiveHearing, ClaimField::setNilled);
    }
      
    public void setPaidInFullValues() {
        super.setPaidInFullValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(counselsCost, ClaimField::setAmendedToSubmitted);

        applyIfNotNull(adjournedHearing, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhTelephone, ClaimField::setAmendedToCalculated);
        applyIfNotNull(cmrhOral, ClaimField::setAmendedToCalculated);
        applyIfNotNull(hoInterview, ClaimField::setAmendedToCalculated);
        applyIfNotNull(substantiveHearing, ClaimField::setAmendedToCalculated);

    }

    @Override
    public boolean getIsCrimeClaim() {
        return false;
    }

    @Override
    public ClaimDetailsView<? extends Claim> toViewModel() {
        return new CivilClaimDetailsView(this);
    }

    @Override
    public AssessmentPost toAssessment(AssessmentMapper mapper, String userId) {
        return mapper.mapCivilClaimToAssessment(this, userId);
    }
}
