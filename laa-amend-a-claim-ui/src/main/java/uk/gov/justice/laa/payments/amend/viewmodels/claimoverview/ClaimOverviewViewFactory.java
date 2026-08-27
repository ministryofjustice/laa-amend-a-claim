package uk.gov.justice.laa.payments.amend.viewmodels.claimoverview;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;

@UtilityClass
public class ClaimOverviewViewFactory {

  public static ClaimOverviewView create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimOverviewView(crimeClaim);
      case CivilClaimDetails civilClaim -> new CivilClaimOverviewView(civilClaim);
      case MediationClaimDetails mediationClaim -> new MediationClaimOverviewView(mediationClaim);
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
