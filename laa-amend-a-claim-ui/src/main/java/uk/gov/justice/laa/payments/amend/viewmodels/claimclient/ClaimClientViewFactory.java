package uk.gov.justice.laa.payments.amend.viewmodels.claimclient;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;

@UtilityClass
public class ClaimClientViewFactory {

  public static ClaimClientView<?> create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimClientView(crimeClaim);
      case CivilClaimDetails civilClaimDetails -> new CivilClaimClientView(civilClaimDetails);
      case MediationClaimDetails mediationClaim -> new MediationClaimClientView(mediationClaim);
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
