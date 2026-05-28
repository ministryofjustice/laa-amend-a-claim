package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

@UtilityClass
public class ClaimCostsViewFactory {

  public static ClaimCostsView create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimCostsView(crimeClaim);
      case CivilClaimDetails civilClaimDetails ->
          throw new IllegalArgumentException("TODO: BC-567 will implement this");
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
