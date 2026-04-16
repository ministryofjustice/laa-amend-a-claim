package uk.gov.justice.laa.amend.claim.models;

import static java.util.Objects.nonNull;

public record MicrosoftApiUser(String id, String displayName, String givenName, String surname) {

    public String name() {
        if (nonNull(givenName) && nonNull(surname)) {
            return String.format("%s %s", givenName, surname);
        }
        return displayName;
    }
}
