package uk.gov.justice.laa.amend.claim.viewmodels.claimclient;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

@UtilityClass
public class ClaimClientViewFactory {

  public static ClaimClientView create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimClientView(crimeClaim);
      case CivilClaimDetails civilClaimDetails ->
          throw new IllegalArgumentException("TODO: BC-567 will implement this");
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
