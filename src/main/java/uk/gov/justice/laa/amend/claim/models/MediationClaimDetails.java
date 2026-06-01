package uk.gov.justice.laa.amend.claim.models;

import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.MediationClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@EqualsAndHashCode(callSuper = true)
@Data
public class MediationClaimDetails extends ClaimDetails {

  private ClaimField detentionTravelWaitingCosts;
  private ClaimField jrFormFillingCost;
  private ClaimField adjournedHearing;
  private ClaimField cmrhTelephone;
  private ClaimField cmrhOral;
  private ClaimField hoInterview;
  private ClaimField substantiveHearing;
  private ClaimField counselsCost;

  private LocalDate clientDateOfBirth;
  private String uniqueClientNumber;
  private String clientPostcode;
  private Boolean isClientLegallyAided;
  private Boolean isClientPostalApplicationAccepted;

  private String client2Forename;
  private String client2Surname;
  private LocalDate client2DateOfBirth;
  private String client2Ucn;
  private String client2Postcode;
  private String client2Gender;
  private String client2Ethnicity;
  private String client2Disability;
  private Boolean isClient2LegallyAided;
  private Boolean isClient2PostalApplicationAccepted;

  @Override
  public boolean isAssessedTotalFieldAssessable() {
    return false;
  }

  @Override
  public ClaimDetailsView<? extends Claim> toViewModel() {
    return new MediationClaimDetailsView(this);
  }

  @Override
  public AssessmentPost toAssessment(AssessmentMapper mapper, String userId) {
    return mapper.mapMediationClaimToAssessment(this, userId);
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
