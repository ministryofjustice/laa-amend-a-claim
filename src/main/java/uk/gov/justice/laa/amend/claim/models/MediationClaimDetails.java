package uk.gov.justice.laa.amend.claim.models;

import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.MediationClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@Slf4j
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

  private String caseId;
  private Integer mediationSessionsCount;
  private Integer mediationTimeMinutes;
  private String outreachLocation;
  private String referralSource;
  private String scheduleReference;
  private String matterType1;
  private String matterType2;

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

  @Override
  public ClaimField getClaimField(Cost cost) throws ClaimMismatchException {
    return switch (cost) {
      case PROFIT_COSTS -> getNetProfitCost();
      case DISBURSEMENTS -> getNetDisbursementAmount();
      case DISBURSEMENTS_VAT -> getDisbursementVatAmount();
      case COUNSEL_COSTS -> getCounselsCost();
      case DETENTION_TRAVEL_AND_WAITING_COSTS -> getDetentionTravelWaitingCosts();
      case JR_FORM_FILLING_COSTS -> getJrFormFillingCost();
      default -> {
        var message = "Claim %s does not support this cost".formatted(getClaimId());
        log.warn(message);
        throw new ClaimMismatchException(message);
      }
    };
  }

  @Override
  public void setClaimField(Cost cost, ClaimField claimField) {
    switch (cost) {
      case PROFIT_COSTS -> setNetProfitCost(claimField);
      case DISBURSEMENTS -> setNetDisbursementAmount(claimField);
      case DISBURSEMENTS_VAT -> setDisbursementVatAmount(claimField);
      case COUNSEL_COSTS -> setCounselsCost(claimField);
      case DETENTION_TRAVEL_AND_WAITING_COSTS -> setDetentionTravelWaitingCosts(claimField);
      case JR_FORM_FILLING_COSTS -> setJrFormFillingCost(claimField);
      default -> {
        var message = "Claim %s does not support this cost".formatted(getClaimId());
        log.warn(message);
        throw new IllegalArgumentException(message);
      }
    }
  }
}
