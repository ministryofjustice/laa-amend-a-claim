package uk.gov.justice.laa.amend.claim.models;

import java.util.stream.Stream;
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
    private String uniqueClientNumber;

    @Override
    public boolean isAssessedTotalFieldAssessable() {
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
                getDetentionTravelWaitingCosts());
    }
}
