package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.removeAmendmentErrors;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.controllers.UserControllerAdvice;

@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/cannot-submit")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@UserControllerAdvice.Enabled
@Controller
public class AmendmentsCannotBeSubmittedController {

  @GetMapping
  public String cannotBeSubmitted(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    getValidClaim(session, submissionId, claimId);

    var errors = getAmendmentErrors(session, claimId);
    if (errors.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No amendment errors found for submission %s claim %s".formatted(submissionId, claimId));
    }
    removeAmendmentErrors(session, claimId);

    model.addAttribute("submissionId", submissionId);
    model.addAttribute("claimId", claimId);
    model.addAttribute("errors", errors);

    return "pages/amendments/cannot-be-submitted";
  }
}
