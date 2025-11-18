package uk.gov.justice.laa.amend.claim.exceptions;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(final String message) {
        super(message);
    }
}
