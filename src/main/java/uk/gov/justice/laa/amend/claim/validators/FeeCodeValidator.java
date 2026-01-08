package uk.gov.justice.laa.amend.claim.validators;

import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

import java.util.Set;

public final class FeeCodeValidator {

    private static final Set<String> VALID_FEE_CODES = Set.of("INVC");

    private FeeCodeValidator() {
        // Prevent instantiation
    }

    /**
     * Checks if the fee code for a claim is invalid.
     *
     * @param claimDetails the claim details to check
     * @return true if the fee code is invalid or null for crime claims, false otherwise
     */
    public static boolean isNotValidFeeCode(ClaimDetails claimDetails) {
        if (claimDetails instanceof CrimeClaimDetails) {
            String feeCode = claimDetails.getFeeCode();
            return feeCode == null || !VALID_FEE_CODES.contains(feeCode);
        }
        return true;
    }

}
