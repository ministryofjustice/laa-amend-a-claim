package uk.gov.justice.laa.amend.claim.models;

import uk.gov.justice.laa.amend.claim.exceptions.ClaimMismatchException;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record ClaimFieldAccessor<T extends ClaimSummary>(
    Class<T> type,
    Function<T, ClaimField> getter,
    BiConsumer<T, ClaimField> setter
) {
    public ClaimField get(ClaimSummary claim) throws ClaimMismatchException {
        if (type.isInstance(claim)) {
            return getter.apply(type.cast(claim));
        } else {
            throw new ClaimMismatchException(String.format("Claim summary object is not of type %s", type.getName()));
        }
    }

    public void set(ClaimSummary summary, ClaimField value) {
        if (type.isInstance(summary)) {
            setter.accept(type.cast(summary), value);
        }
    }
}
