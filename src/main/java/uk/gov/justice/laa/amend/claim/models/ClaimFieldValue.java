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

    record Null() implements ClaimFieldValue, Serializable {
        @Override
        public Object getValue() {
            return null;
        }
    }

    static ClaimFieldValue of(Object value, boolean needsAddingIfNull) {
        if (value != null) {
            return new Value(value);
        }
        if (needsAddingIfNull) {
            return new NeedsAdding();
        }
        return new Null();
    }

    static ClaimFieldValue of(Object value) {
        return of(value, false);
    }
}
