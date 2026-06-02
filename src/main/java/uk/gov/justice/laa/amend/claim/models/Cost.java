package uk.gov.justice.laa.amend.claim.models;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;

@Slf4j
@Getter
public enum Cost {
  PROFIT_COSTS("profit-costs", "profitCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) {
      return claim.getNetProfitCost();
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      claim.setNetProfitCost(field);
    }
  },

  DISBURSEMENTS("disbursements", "disbursements") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) {
      return claim.getNetDisbursementAmount();
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      claim.setNetDisbursementAmount(field);
    }
  },

  DISBURSEMENTS_VAT("disbursements-vat", "disbursementsVat") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) {
      return claim.getDisbursementVatAmount();
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      claim.setDisbursementVatAmount(field);
    }
  },

  COUNSEL_COSTS("counsel-costs", "counselCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException {
      return switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.getCounselsCost();
        case MediationClaimDetails mediationClaim -> mediationClaim.getCounselsCost();
        default -> throwMismatch(claim);
      };
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.setCounselsCost(field);
        case MediationClaimDetails mediationClaim -> mediationClaim.setCounselsCost(field);
        default -> throwMismatchUnchecked(claim);
      }
    }
  },

  DETENTION_TRAVEL_AND_WAITING_COSTS(
      "detention-travel-and-waiting-costs", "detentionTravelAndWaitingCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException {
      return switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.getDetentionTravelWaitingCosts();
        case MediationClaimDetails mediationClaim ->
            mediationClaim.getDetentionTravelWaitingCosts();
        default -> throwMismatch(claim);
      };
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.setDetentionTravelWaitingCosts(field);
        case MediationClaimDetails mediationClaim ->
            mediationClaim.setDetentionTravelWaitingCosts(field);
        default -> throwMismatchUnchecked(claim);
      }
    }
  },

  JR_FORM_FILLING_COSTS("jr-form-filling-costs", "jrFormFillingCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException {
      return switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.getJrFormFillingCost();
        case MediationClaimDetails mediationClaim -> mediationClaim.getJrFormFillingCost();
        default -> throwMismatch(claim);
      };
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      switch (claim) {
        case CivilClaimDetails civilClaim -> civilClaim.setJrFormFillingCost(field);
        case MediationClaimDetails mediationClaim -> mediationClaim.setJrFormFillingCost(field);
        default -> throwMismatchUnchecked(claim);
      }
    }
  },

  TRAVEL_COSTS("travel-costs", "travelCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException {
      if (claim instanceof CrimeClaimDetails crimeClaim) {
        return crimeClaim.getTravelCosts();
      }
      return throwMismatch(claim);
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      if (claim instanceof CrimeClaimDetails crimeClaim) {
        crimeClaim.setTravelCosts(field);
      } else {
        throwMismatchUnchecked(claim);
      }
    }
  },

  WAITING_COSTS("waiting-costs", "waitingCosts") {
    @Override
    public ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException {
      if (claim instanceof CrimeClaimDetails crimeClaim) {
        return crimeClaim.getWaitingCosts();
      }
      return throwMismatch(claim);
    }

    @Override
    public void setClaimField(ClaimDetails claim, ClaimField field) {
      if (claim instanceof CrimeClaimDetails crimeClaim) {
        crimeClaim.setWaitingCosts(field);
      } else {
        throwMismatchUnchecked(claim);
      }
    }
  };

  private final String path;
  private final String prefix;

  Cost(String path, String prefix) {
    this.path = path;
    this.prefix = prefix;
  }

  public abstract ClaimField getClaimField(ClaimDetails claim) throws ClaimMismatchException;

  public abstract void setClaimField(ClaimDetails claim, ClaimField field);

  public String getChangeUrl() {
    return "/submissions/%s/claims/%s/" + path;
  }

  public static Cost fromPath(String path) {
    return Arrays.stream(values())
        .filter(cost -> cost.path.equals(path))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  private static ClaimField throwMismatch(ClaimDetails claim) throws ClaimMismatchException {
    var message = "Claim %s does not support this cost".formatted(claim.getClaimId());
    log.warn(message);
    throw new ClaimMismatchException(message);
  }

  private static void throwMismatchUnchecked(ClaimDetails claim) {
    var message = "Claim %s does not support this cost".formatted(claim.getClaimId());
    log.warn(message);
    throw new IllegalArgumentException(message);
  }
}
