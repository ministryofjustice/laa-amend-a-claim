package uk.gov.justice.laa.amend.claim.models;

import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record ClaimFieldAccessor<T extends ClaimSummary>(
    Class<T> type,
    Function<T, ClaimFieldRow> getter,
    BiConsumer<T, ClaimFieldRow> setter
) {
    public ClaimFieldRow get(ClaimSummary summary) {
        if (type.isInstance(summary)) {
            return getter.apply(type.cast(summary));
        }
        return null;
    }

    public void set(ClaimSummary summary, ClaimFieldRow value) {
        if (type.isInstance(summary)) {
            setter.accept(type.cast(summary), value);
        }
    }
}
