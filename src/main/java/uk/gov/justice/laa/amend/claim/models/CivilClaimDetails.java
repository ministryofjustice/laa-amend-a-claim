package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.util.stream.Stream;

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

        // Bolt-ons get set to 0
        applyIfNotNull(adjournedHearing, ClaimField::setNilled);
        applyIfNotNull(cmrhTelephone, ClaimField::setNilled);
        applyIfNotNull(cmrhOral, ClaimField::setNilled);
        applyIfNotNull(hoInterview, ClaimField::setNilled);
        applyIfNotNull(substantiveHearing, ClaimField::setNilled);
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(counselsCost, ClaimField::setAssessedToCalculated);
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToCalculated);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToCalculated);

        // Bolt-ons get set to calculated
        applyIfNotNull(adjournedHearing, ClaimField::setAssessedToCalculated);
        applyIfNotNull(cmrhTelephone, ClaimField::setAssessedToCalculated);
        applyIfNotNull(cmrhOral, ClaimField::setAssessedToCalculated);
        applyIfNotNull(hoInterview, ClaimField::setAssessedToCalculated);
        applyIfNotNull(substantiveHearing, ClaimField::setAssessedToCalculated);
    }

    @Override
    public void setReducedValues() {
        super.setReducedValues();
        applyIfNotNull(counselsCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToSubmitted);

        // Bolt-ons get set to null
        applyIfNotNull(adjournedHearing, ClaimField::setAssessedToNull);
        applyIfNotNull(cmrhTelephone, ClaimField::setAssessedToNull);
        applyIfNotNull(cmrhOral, ClaimField::setAssessedToNull);
        applyIfNotNull(hoInterview, ClaimField::setAssessedToNull);
        applyIfNotNull(substantiveHearing, ClaimField::setAssessedToNull);
    }

    public void setPaidInFullValues() {
        super.setPaidInFullValues();
        applyIfNotNull(counselsCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToSubmitted);

        // Bolt-ons get set to null
        applyIfNotNull(adjournedHearing, ClaimField::setAssessedToNull);
        applyIfNotNull(cmrhTelephone, ClaimField::setAssessedToNull);
        applyIfNotNull(cmrhOral, ClaimField::setAssessedToNull);
        applyIfNotNull(hoInterview, ClaimField::setAssessedToNull);
        applyIfNotNull(substantiveHearing, ClaimField::setAssessedToNull);
    }

    @Override
    public boolean isAssessedTotalFieldModifiable() {
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

    @Override
    protected Stream<ClaimField> specificClaimFields() {
        return Stream.of(
            getHoInterview(),
            getSubstantiveHearing(),
            getCounselsCost(),
            getJrFormFillingCost(),
            getAdjournedHearing(),
            getCmrhOral(),
            getCmrhTelephone(),
            getDetentionTravelWaitingCosts()
        );
    }
}
