package uk.gov.justice.laa.amend.claim.models;

import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.VALID_POLICE_STATION_FEE_CODES;

import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class CrimeClaimDetails extends ClaimDetails {

  private ClaimField travelCosts;
  private ClaimField waitingCosts;
  private String schemeId;
  private LocalDate representationOrderDate;
  private String standardFeeCategory;
  private Integer suspectsDefendantsCount;
  private Integer policeStationCourtAttendancesCount;
  private String policeStationCourtPrisonId;
  private String dsccNumber;
  private String maatId;
  private String prisonLawPriorApprovalNumber;
  private Boolean isDutySolicitor;
  private Boolean isYouthCourt;

  @Override
  public boolean isAssessedTotalFieldAssessable() {
    return this.getFeeCode() != null && VALID_POLICE_STATION_FEE_CODES.contains(this.getFeeCode());
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
    return Stream.of(getTravelCosts(), getWaitingCosts());
  }

  @Override
  public ClaimField getClaimField(Cost cost) throws ClaimMismatchException {
    return switch (cost) {
      case PROFIT_COSTS -> getNetProfitCost();
      case DISBURSEMENTS -> getNetDisbursementAmount();
      case DISBURSEMENTS_VAT -> getDisbursementVatAmount();
      case TRAVEL_COSTS -> getTravelCosts();
      case WAITING_COSTS -> getWaitingCosts();
      default -> {
        var message = "Claim %s does not support cost %s".formatted(getClaimId(), cost);
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
      case TRAVEL_COSTS -> setTravelCosts(claimField);
      case WAITING_COSTS -> setWaitingCosts(claimField);
      default -> {
        var message = "Claim %s does not support cost %s".formatted(getClaimId(), cost);
        log.warn(message);
        throw new IllegalArgumentException(message);
      }
    }
  }
}
