package uk.gov.justice.laa.amend.claim.models;


import lombok.Getter;

@Getter
public enum OutcomeType {
    PAID_IN_FULL("outcome.paidInFull", "paid-in-full", true),
    REDUCED("outcome.reduced", "reduced-still-escaped", true),
    REDUCED_TO_FIXED_FEE("outcome.reducedToFixedFee", "reduced-to-fixed-fee-assessed", true),
    NILLED("outcome.nilled", "nilled", false);

    private final String messageKey;
    private final String formValue;
    private final Boolean canAmendCosts;

    OutcomeType(String messageKey, String formValue, Boolean canAmendCosts) {
        this.messageKey = messageKey;
        this.formValue = formValue;
        this.canAmendCosts = canAmendCosts;
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
