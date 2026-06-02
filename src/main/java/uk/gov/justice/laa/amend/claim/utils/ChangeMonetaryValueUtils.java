package uk.gov.justice.laa.amend.claim.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;

@Slf4j
@UtilityClass
public class ChangeMonetaryValueUtils {
  public ClaimField getCostClaimField(ClaimDetails claim, Cost cost) throws ClaimMismatchException {
    return switch (cost) {
      case PROFIT_COSTS -> getProfitCost(claim);
      case DISBURSEMENTS -> getDisbursements(claim);
      case DISBURSEMENTS_VAT -> getDisbursementsVat(claim);
      case COUNSEL_COSTS -> getCounselCosts(claim);
      case DETENTION_TRAVEL_AND_WAITING_COSTS -> getDetentionTravelAndWaitingCosts(claim);
      case JR_FORM_FILLING_COSTS -> getJrFormFillingCosts(claim);
      case TRAVEL_COSTS -> getTravelCosts(claim);
      case WAITING_COSTS -> getWaitingCosts(claim);
    };
  }

  public void setCostClaimField(ClaimDetails claim, Cost cost, ClaimField claimField) {
    switch (cost) {
      case PROFIT_COSTS -> setProfitCost(claim, claimField);
      case DISBURSEMENTS -> setDisbursements(claim, claimField);
      case DISBURSEMENTS_VAT -> setDisbursementsVat(claim, claimField);
      case COUNSEL_COSTS -> setCounselCosts(claim, claimField);
      case DETENTION_TRAVEL_AND_WAITING_COSTS ->
          setDetentionTravelAndWaitingCosts(claim, claimField);
      case JR_FORM_FILLING_COSTS -> setJrFormFillingCosts(claim, claimField);
      case TRAVEL_COSTS -> setTravelCosts(claim, claimField);
      case WAITING_COSTS -> setWaitingCosts(claim, claimField);
      default -> throw new RuntimeException("Unknown cost type: " + cost);
    }
  }

  private static ClaimField getProfitCost(ClaimDetails claim) {
    return claim.getNetProfitCost();
  }

  private static void setProfitCost(ClaimDetails claim, ClaimField claimField) {
    claim.setNetProfitCost(claimField);
  }

  private static ClaimField getDisbursements(ClaimDetails claim) {
    return claim.getNetDisbursementAmount();
  }

  private static void setDisbursements(ClaimDetails claim, ClaimField claimField) {
    claim.setNetDisbursementAmount(claimField);
  }

  private static ClaimField getDisbursementsVat(ClaimDetails claim) {
    return claim.getDisbursementVatAmount();
  }

  private static void setDisbursementsVat(ClaimDetails claim, ClaimField claimField) {
    claim.setDisbursementVatAmount(claimField);
  }

  private static ClaimField getCounselCosts(ClaimDetails claim) throws ClaimMismatchException {
    return switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.getCounselsCost();
      case MediationClaimDetails mediationClaim -> mediationClaim.getCounselsCost();
      default -> throwClaimMismatchException(claim);
    };
  }

  private static void setCounselCosts(ClaimDetails claim, ClaimField claimField) {
    switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.setCounselsCost(claimField);
      case MediationClaimDetails mediationClaim -> mediationClaim.setCounselsCost(claimField);
      default -> {}
    }
  }

  private static ClaimField getDetentionTravelAndWaitingCosts(ClaimDetails claim)
      throws ClaimMismatchException {
    return switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.getDetentionTravelWaitingCosts();
      case MediationClaimDetails mediationClaim -> mediationClaim.getDetentionTravelWaitingCosts();
      default -> throwClaimMismatchException(claim);
    };
  }

  private static void setDetentionTravelAndWaitingCosts(ClaimDetails claim, ClaimField claimField) {
    switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.setDetentionTravelWaitingCosts(claimField);
      case MediationClaimDetails mediationClaim ->
          mediationClaim.setDetentionTravelWaitingCosts(claimField);
      default -> {}
    }
  }

  private static ClaimField getJrFormFillingCosts(ClaimDetails claim)
      throws ClaimMismatchException {
    return switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.getJrFormFillingCost();
      case MediationClaimDetails mediationClaim -> mediationClaim.getJrFormFillingCost();
      default -> throwClaimMismatchException(claim);
    };
  }

  private static void setJrFormFillingCosts(ClaimDetails claim, ClaimField claimField) {
    switch (claim) {
      case CivilClaimDetails civilClaim -> civilClaim.setJrFormFillingCost(claimField);
      case MediationClaimDetails mediationClaim -> mediationClaim.setJrFormFillingCost(claimField);
      default -> {}
    }
  }

  private static ClaimField getTravelCosts(ClaimDetails claim) throws ClaimMismatchException {
    if (claim instanceof CrimeClaimDetails crimeClaim) {
      return crimeClaim.getTravelCosts();
    }
    return throwClaimMismatchException(claim);
  }

  private static void setTravelCosts(ClaimDetails claim, ClaimField claimField) {
    if (claim instanceof CrimeClaimDetails crimeClaim) {
      crimeClaim.setTravelCosts(claimField);
    }
  }

  private static ClaimField getWaitingCosts(ClaimDetails claim) throws ClaimMismatchException {
    if (claim instanceof CrimeClaimDetails crimeClaim) {
      return crimeClaim.getWaitingCosts();
    }
    return throwClaimMismatchException(claim);
  }

  private static void setWaitingCosts(ClaimDetails claim, ClaimField claimField) {
    if (claim instanceof CrimeClaimDetails crimeClaim) {
      crimeClaim.setWaitingCosts(claimField);
    }
  }

  private static ClaimField throwClaimMismatchException(ClaimDetails claim)
      throws ClaimMismatchException {
    var message = String.format("Claim %s does not support costs field", claim.getClaimId());
    log.warn(message);
    throw new ClaimMismatchException(message);
  }
}
