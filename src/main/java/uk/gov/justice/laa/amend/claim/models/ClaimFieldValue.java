package uk.gov.justice.laa.amend.claim.models;

import java.io.Serializable;

public sealed interface ClaimFieldValue extends Serializable {

    Object getValue();

    record Value(Object value) implements ClaimFieldValue {
        @Override
        public Object getValue() {
            return value;
        }
    }

    record NeedsAdding() implements ClaimFieldValue {
        @Override
        public Object getValue() {
            return null;
        }
    }

    static ClaimFieldValue of(Object value) {
        return new Value(value);
    }
}
