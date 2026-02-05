package uk.gov.justice.laa.amend.claim.models;

import java.util.function.BiConsumer;
import java.util.function.Function;
import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;

public record ClaimFieldAccessor<T extends Claim>(
        Class<T> type, Function<T, ClaimField> getter, BiConsumer<T, ClaimField> setter) {
    public ClaimField get(Claim claim) throws ClaimMismatchException {
        if (type.isInstance(claim)) {
            return getter.apply(type.cast(claim));
        } else {
            throw new ClaimMismatchException(String.format("Claim summary object is not of type %s", type.getName()));
        }
    }

    public void set(Claim summary, ClaimField value) {
        if (type.isInstance(summary)) {
            setter.accept(type.cast(summary), value);
        }
    }
}
