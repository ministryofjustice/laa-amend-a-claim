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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.service.CheckService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/check")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@Controller
public class CheckAmendmentsController {

  private final CheckService checkService;

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

    model.addAttribute("forms", amendmentForms);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    model.addAttribute(
        "client2Form",
        amendmentForms.getClient2Form() != null
            ? amendmentForms.getClient2Form().getCurrent()
            : null);
    model.addAttribute("clientView", clientView);
    model.addAttribute("areaOfLaw", claim.getAreaOfLaw());

    return "amendments/check-your-answers";
  }

  @PostMapping
  public String submit(
      HttpSession session,
      @PathVariable("submissionId") UUID submissionId,
      @PathVariable("claimId") UUID claimId) {

    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    if (!amendmentForms.hasAmendments()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No amendments found for submission %s claim %s".formatted(submissionId, claimId));
    }

    checkService.submitAmendments(submissionId, claimId, UUID.randomUUID(), claim, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/success".formatted(submissionId, claimId);
  }
}
