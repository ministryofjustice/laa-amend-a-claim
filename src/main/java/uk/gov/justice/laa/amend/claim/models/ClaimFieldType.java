package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum ClaimFieldType {
    NORMAL,
    BOLT_ON,
    ASSESSED,
    ALLOWED,
    TOTAL,
    FIXED_FEE;

    public boolean isNotAssessable() {
        return this == BOLT_ON || this == TOTAL || this == FIXED_FEE;
    }
}
