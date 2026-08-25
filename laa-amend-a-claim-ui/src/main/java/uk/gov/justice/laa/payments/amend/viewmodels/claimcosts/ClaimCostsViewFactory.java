package uk.gov.justice.laa.payments.amend.viewmodels.claimcosts;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;

@UtilityClass
public class ClaimCostsViewFactory {

  public static ClaimCostsView create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimCostsView(crimeClaim);
      case CivilClaimDetails civilClaim -> new CivilClaimCostsView(civilClaim);
      case MediationClaimDetails mediationClaim -> new MediationClaimCostsView(mediationClaim);
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
