package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

/**
 * Claim field types
 * BOLT ON: A bolt on field
 * ASSESSED_TOTAL: An assessed total field
 * ALLOWED_TOTAL: An allowed total field
 * CALCULATED_TOTAL: A non-assessed total i.e. calculated prior to an assessment
 * FIXED_FEE: A fixed fee field
 * OTHER: Any other field
 */
@Getter
public enum ClaimFieldType {
    BOLT_ON,
    ASSESSED_TOTAL,
    ALLOWED_TOTAL,
    CALCULATED_TOTAL,
    FIXED_FEE,
    OTHER;

    public boolean isNotAssessable() {
        return this == BOLT_ON || this == CALCULATED_TOTAL || this == FIXED_FEE;
    }
}
