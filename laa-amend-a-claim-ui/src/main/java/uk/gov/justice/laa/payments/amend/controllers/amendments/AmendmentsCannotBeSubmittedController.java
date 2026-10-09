package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.removeAmendmentErrors;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
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
import uk.gov.justice.laa.payments.amend.models.AmendmentError;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.amendmenterrors.AmendmentErrorRowsFactory;

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
    var claim = getValidClaim(session, submissionId, claimId);
    var errors = takeAmendmentErrors(session, submissionId, claimId);

    model.addAttribute("submissionId", submissionId);
    model.addAttribute("claimId", claimId);

    return AmendmentError.anyClaimModified(errors)
        ? claimModified(session, model)
        : listErrors(model, claim, errors);
  }

  private static String claimModified(HttpSession session, Model model) {
    String searchUrl = (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
    model.addAttribute("searchUrl", searchUrl);
    return "pages/amendments/claim-modified";
  }

  private static String listErrors(Model model, ClaimDetails claim, List<AmendmentError> errors) {
    model.addAttribute("errorRows", AmendmentErrorRowsFactory.create(claim, errors));
    return "pages/amendments/cannot-be-submitted";
  }

  private static List<AmendmentError> takeAmendmentErrors(
      HttpSession session, UUID submissionId, UUID claimId) {
    var errors = getAmendmentErrors(session, claimId);
    if (errors.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No amendment errors found for submission %s claim %s".formatted(submissionId, claimId));
    }
    removeAmendmentErrors(session, claimId);
    return errors;
  }
}
