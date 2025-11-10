package uk.gov.justice.laa.amend.claim.models;


public enum OutcomeType {
    PAID_IN_FULL("outcome.paidInFull"),
    REDUCED("outcome.reduced"),
    REDUCED_TO_FIXED_FEE("outcome.reducedToFixedFee"),
    NILLED("outcome.nilled");

    private final String messageKey;

    OutcomeType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
