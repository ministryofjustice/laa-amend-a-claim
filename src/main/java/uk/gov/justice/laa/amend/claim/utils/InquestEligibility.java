package uk.gov.justice.laa.amend.claim.utils;

import java.util.Set;
import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.amend.claim.models.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@UtilityClass
public class InquestEligibility {

  /**
   * Determines whether a claim is inquest-eligible: Legal Help area of law with a matter type in
   * the configured set of inquest matter type codes.
   */
  public static boolean isEligible(ClaimDetails claim, Set<String> inquestMatterTypeCodes) {
    if (claim.getAreaOfLaw() != AreaOfLaw.LEGAL_HELP
        || !(claim instanceof CivilClaimDetails civilClaimDetails)) {
      return false;
    }
    return inquestMatterTypeCodes != null
        && inquestMatterTypeCodes.contains(civilClaimDetails.getMatterType1());
  }
}
