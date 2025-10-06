package uk.gov.justice.laa.amend.claim.viewmodels;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** View model for handling a pagination link (Previous or Next). */
@Getter
@AllArgsConstructor
public class PaginationLink {
    private final String text;
    private final String href;
}
