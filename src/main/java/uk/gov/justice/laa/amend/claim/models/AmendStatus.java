package uk.gov.justice.laa.amend.claim.models;

import lombok.Getter;

@Getter
public enum AmendStatus {
    AMENDABLE,
    NOT_AMENDABLE,
    NEEDS_AMENDING,
    DO_NOT_DISPLAY
}
