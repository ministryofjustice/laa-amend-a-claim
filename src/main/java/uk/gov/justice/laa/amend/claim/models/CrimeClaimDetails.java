package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimDetails extends ClaimDetails {

    private String policeStationCourtPrisonId;
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
        applyIfNotNull(travelCosts, ClaimField::setAmendedToSubmitted);
        applyIfNotNull(waitingCosts, ClaimField::setAmendedToSubmitted);

        if (getPoliceStationCourtPrisonId() != null) {
            applyIfNotNull(getAssessedTotalVat(), ClaimField::setToNeedsAmending);
            applyIfNotNull(getAssessedTotalInclVat(), ClaimField::setToNeedsAmending);
        } else {
            applyIfNotNull(getAssessedTotalVat(), ClaimField::setToNull);
            applyIfNotNull(getAssessedTotalInclVat(), ClaimField::setToNull);
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
