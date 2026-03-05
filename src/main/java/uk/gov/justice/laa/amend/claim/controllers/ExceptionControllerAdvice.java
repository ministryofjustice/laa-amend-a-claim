package uk.gov.justice.laa.amend.claim.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.justice.laa.amend.claim.exceptions.NoClaimInSessionException;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(NoClaimInSessionException.class)
    public String handle(NoClaimInSessionException ex) {
        return String.format("redirect:/submissions/%s/claims/%s", ex.getSubmissionId(), ex.getClaimId());
    }
}
