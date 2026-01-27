package uk.gov.justice.laa.amend.claim.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MicrosoftApiUser {
    private String id;
    private String displayName;
    private String givenName;
    private String surname;

    public String getName() {
        if (givenName != null && surname != null) {
            return String.format("%s %s", givenName, surname);
        }
        return displayName;
    }
}
