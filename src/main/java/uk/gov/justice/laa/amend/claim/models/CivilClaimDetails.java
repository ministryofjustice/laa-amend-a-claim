package uk.gov.justice.laa.amend.claim.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

import java.util.List;
import java.util.Objects;
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
        applyIfNotNull(adjournedHearing, ClaimField::setNilled);
        applyIfNotNull(cmrhTelephone, ClaimField::setNilled);
        applyIfNotNull(cmrhOral, ClaimField::setNilled);
        applyIfNotNull(hoInterview, ClaimField::setNilled);
        applyIfNotNull(substantiveHearing, ClaimField::setNilled);
    }

    @Override
    public void setReducedToFixedFeeValues() {
        super.setReducedToFixedFeeValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToCalculated);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToCalculated);
        applyIfNotNull(adjournedHearing, ClaimField::setAssessedToCalculated);
        applyIfNotNull(cmrhTelephone, ClaimField::setAssessedToCalculated);
        applyIfNotNull(cmrhOral, ClaimField::setAssessedToCalculated);
        applyIfNotNull(hoInterview, ClaimField::setAssessedToCalculated);
        applyIfNotNull(substantiveHearing, ClaimField::setAssessedToCalculated);
        applyIfNotNull(counselsCost, ClaimField::setAssessedToCalculated);
    }

    @Override
    public void setReducedValues() {
        super.setReducedValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(counselsCost, ClaimField::setAssessedToSubmitted);

        // Bolt-ons get set to 0
        applyIfNotNull(adjournedHearing, ClaimField::setNilled);
        applyIfNotNull(cmrhTelephone, ClaimField::setNilled);
        applyIfNotNull(cmrhOral, ClaimField::setNilled);
        applyIfNotNull(hoInterview, ClaimField::setNilled);
        applyIfNotNull(substantiveHearing, ClaimField::setNilled);
    }
      
    public void setPaidInFullValues() {
        super.setPaidInFullValues();
        applyIfNotNull(detentionTravelWaitingCosts, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(jrFormFillingCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(counselsCost, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(adjournedHearing, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(cmrhTelephone, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(cmrhOral, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(hoInterview, ClaimField::setAssessedToSubmitted);
        applyIfNotNull(substantiveHearing, ClaimField::setAssessedToSubmitted);

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

    @Override
    public List<ClaimField> getClaimFields() {
        return Stream.concat(
                        super.commonClaimFieldsStream(),
                        civilSpecificFieldsStream()
                )
                .filter(Objects::nonNull)
                .toList();
    }

    protected Stream<ClaimField> civilSpecificFieldsStream() {
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
