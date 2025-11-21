package uk.gov.justice.laa.amend.claim.models;

import java.io.Serializable;

public sealed interface ClaimFieldValue {

    Object getValue();

    record Value(Object value) implements ClaimFieldValue, Serializable {
        @Override
        public Object getValue() {
            return value;
        }
    }

    record NeedsAdding() implements ClaimFieldValue, Serializable {
        @Override
        public Object getValue() {
            return null;
        }
    }

    static ClaimFieldValue of(Object value, boolean needsAddingIfNull) {
        if (needsAddingIfNull) {
            return new NeedsAdding();
        }
        return new Value(value);
    }

    static ClaimFieldValue of(Object value) {
        return of(value, false);
    }
}
