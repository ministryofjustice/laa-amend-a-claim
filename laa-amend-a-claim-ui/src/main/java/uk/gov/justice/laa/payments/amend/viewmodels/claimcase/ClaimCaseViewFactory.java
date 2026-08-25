package uk.gov.justice.laa.payments.amend.viewmodels.claimcase;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;

@UtilityClass
public class ClaimCaseViewFactory {

  public static ClaimCaseView<?> create(ClaimDetails claim) {
    return switch (claim) {
      case CrimeClaimDetails crimeClaim -> new CrimeClaimCaseView(crimeClaim);
      case CivilClaimDetails civilClaimDetails -> new CivilClaimCaseView(civilClaimDetails);
      case MediationClaimDetails mediationClaim -> new MediationClaimCaseView(mediationClaim);
      default -> throw new IllegalArgumentException("Unsupported ClaimDetails type");
    };
  }
}
