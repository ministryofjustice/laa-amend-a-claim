package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum OutcomeType {
    PAID_IN_FULL("outcome.paidInFull", "paid-in-full"),
    REDUCED("outcome.reduced", "reduced-still-escaped"),
    REDUCED_TO_FIXED_FEE("outcome.reducedToFixedFee", "reduced-to-fixed-fee-assessed"),
    NILLED("outcome.nilled", "nilled");

    private final String messageKey;
    private final String formValue;

    OutcomeType(String messageKey, String formValue) {
        this.messageKey = messageKey;
        this.formValue = formValue;
    }

    /**
     * Parses a form value string to an OutcomeType enum.
     *
     * @param formValue the form value (e.g., "reduced-to-fixed-fee-assessed")
     * @return the corresponding OutcomeType, or null if not found
     */
    public static OutcomeType fromFormValue(String formValue) {
        if (formValue == null) {
            return null;
        }
        for (OutcomeType type : values()) {
            if (type.formValue.equals(formValue)) {
                return type;
            }
        }
        return null;
    }
}
