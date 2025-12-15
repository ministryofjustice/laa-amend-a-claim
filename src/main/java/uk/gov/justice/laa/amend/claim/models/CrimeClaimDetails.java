package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.util.stream.Stream;

import static uk.gov.justice.laa.amend.claim.validators.FeeCodeValidator.isNotValidFeeCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimDetails extends ClaimDetails {

    private ClaimField travelCosts;
    private ClaimField waitingCosts;
    private String policeStationCourtPrisonId;
    private String schemeId;

    @Override
    public void setNilledValues() {
        super.setNilledValues();
        applyIfNotNull(travelCosts, ClaimField::setNilled);
        applyIfNotNull(waitingCosts, ClaimField::setNilled);
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(travelCosts, ClaimField::setAssessedToCalculated);
        applyIfNotNull(waitingCosts, ClaimField::setAssessedToCalculated);
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

        applyIfNotNull(travelCosts, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(waitingCosts, ClaimField::setAssessedToSubmitted);

        // assessed total fields are only shown on crime claims if the claim has a certain fee code (e.g. INVC)
        if (isNotValidFeeCode(this)) {
            applyIfNotNull(assessedTotalVat, ClaimField::setToNeedsAssessing);
            applyIfNotNull(assessedTotalInclVat, ClaimField::setToNeedsAssessing);
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

    @Override
    protected Stream<ClaimField> specificClaimFields() {
        return Stream.of(
            getTravelCosts(),
            getWaitingCosts()
        );
    }
}
