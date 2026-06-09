package uk.gov.justice.laa.amend.claim.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@Slf4j
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

  private ClaimField travelAndWaitingCosts;
  private ClaimField isLondonRate;
  private ClaimField priorAuthorityReference;

  private String uniqueClientNumber;
  private LocalDate clientDateOfBirth;
  private String clientPostcode;
  private Boolean isEligibleClient;
  private String clientType;
  private String homeOfficeClientNumber;

  private String scheduleReference;
  private String caseId;
  private String caseReferenceNumber;
  private LocalDate caseStartDate;
  private LocalDate caseConcludedDate;
  private String uniqueFileNumber;
  private String caseStage;
  private BigDecimal valueOfCosts;
  private String procurementArea;
  private String accessPoint;
  private String stageReached;
  private String outcome;
  private String exceptionalCaseFundingReference;
  private String civilLegalAdviceReference;
  private String civilLegalAdviceExemption;
  private String deliveryLocation;
  private String courtLocation;
  private String aitHearingCentre;
  private String localAuthorityNumber;
  private String designatedAccreditedRepresentative;
  private Integer adviceTime;
  private Integer travelTime;
  private Integer waitingTime;
  private Boolean isAdditionalTravelPayment;
  private String followOnWork;
  private Boolean isToleranceApplicable;
  private Boolean isLegacyCase;
  private String meetingsAttended;
  private String adviceType;
  private LocalDate transferDate;
  private Integer medicalReportsClaimed;
  private String exemptionCriteriaSatisfied;
  private Boolean isIrcSurgery;
  private LocalDate surgeryDate;
  private Integer surgeryClientsCount;
  private Integer surgeryMattersCount;
  private Boolean isPostalApplication;
  private String mentalHealthTribunalReference;
  private Boolean isNrmAdvice;
  private String matterType1;
  private String matterType2;

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
  public ClaimField getClaimField(Cost cost) throws ClaimMismatchException {
    return switch (cost) {
      case PROFIT_COSTS -> getNetProfitCost();
      case DISBURSEMENTS -> getNetDisbursementAmount();
      case DISBURSEMENTS_VAT -> getDisbursementVatAmount();
      case COUNSEL_COSTS -> getCounselsCost();
      case DETENTION_TRAVEL_AND_WAITING_COSTS -> getDetentionTravelWaitingCosts();
      case JR_FORM_FILLING_COSTS -> getJrFormFillingCost();
      case TRAVEL_AND_WAITING_COSTS -> getTravelAndWaitingCosts();
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
      case TRAVEL_AND_WAITING_COSTS -> setTravelAndWaitingCosts(claimField);
      default -> {
        var message = "Claim %s does not support this cost".formatted(getClaimId());
        log.warn(message);
        throw new IllegalArgumentException(message);
      }
    }
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
        getDetentionTravelWaitingCosts(),
        getTravelAndWaitingCosts(),
        getPriorAuthorityReference(),
        getIsLondonRate());
  }
}
