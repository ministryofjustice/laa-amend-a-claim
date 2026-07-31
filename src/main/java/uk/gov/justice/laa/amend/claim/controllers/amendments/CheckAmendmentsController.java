package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.controllers.UserControllerAdvice;
import uk.gov.justice.laa.amend.claim.service.CheckAmendmentsService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcosts.ClaimCostsViewFactory;

@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/check")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@UserControllerAdvice.Enabled
@Controller
public class CheckAmendmentsController {

  private final CheckAmendmentsService checkAmendmentsService;

  @GetMapping
  public String check(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    if (!amendmentForms.hasAmendments()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No amendments found for submission %s claim %s".formatted(submissionId, claimId));
    }

    var clientView = ClaimClientViewFactory.create(claim);
    var caseView = ClaimCaseViewFactory.create(claim);
    var costFields = ClaimCostsViewFactory.create(claim).costFields();

    model.addAttribute("forms", amendmentForms);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    model.addAttribute(
        "client2Form",
        amendmentForms.getClient2Form() != null
            ? amendmentForms.getClient2Form().getCurrent()
            : null);
    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());
    model.addAttribute("caseDetailsForm", amendmentForms.getCaseDetailsForm().getCurrent());
    model.addAttribute("costsForm", amendmentForms.getCostsForm().getCurrent());
    model.addAttribute("costFields", costFields);
    model.addAttribute("clientView", clientView);
    model.addAttribute("claim", caseView);
    model.addAttribute("areaOfLaw", claim.getAreaOfLaw());

    return "amendments/check-your-answers";
  }

  @PostMapping
  public String submit(
      HttpSession session,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId,
      @ModelAttribute("userId") UUID userId) {

    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    if (!amendmentForms.hasAmendments()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No amendments found for submission %s claim %s".formatted(submissionId, claimId));
    }

    checkAmendmentsService.submitAmendments(submissionId, claimId, userId, claim, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/confirmation"
        .formatted(submissionId, claimId);
  }
}
