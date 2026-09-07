package uk.gov.justice.laa.payments.amend.controllers;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentErrors;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.justice.laa.payments.amend.exceptions.AmendmentSubmissionFailedException;
import uk.gov.justice.laa.payments.amend.exceptions.NoClaimInSessionException;

@ControllerAdvice
public class ExceptionControllerAdvice {
  @ExceptionHandler(NoClaimInSessionException.class)
  public String handle(NoClaimInSessionException ex) {
    return String.format(
        "redirect:/submissions/%s/claims/%s", ex.getSubmissionId(), ex.getClaimId());
  }

  @ExceptionHandler(AmendmentSubmissionFailedException.class)
  public String handle(AmendmentSubmissionFailedException ex, HttpSession session) {
    saveAmendmentErrors(session, ex.getClaimId(), ex.getErrorMessages());
    return String.format(
        "redirect:/submissions/%s/claims/%s/amendments/cannot-submit",
        ex.getSubmissionId(), ex.getClaimId());
  }
}
