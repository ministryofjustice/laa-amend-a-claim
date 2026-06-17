package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidAmendableClaim;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@HasRoleClaimAmendmentsCaseworker
@RequiredArgsConstructor
public class ClientController {

  private final FeatureFlagsConfig featureFlagsConfig;

  @GetMapping("/client")
  public String viewClient(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    featureFlagsConfig.checkClaimAmendmentEnabled();

    var claim = getValidAmendableClaim(session, submissionId, claimId);
    var clientView = ClaimClientViewFactory.create(claim);
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("clientView", clientView);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    model.addAttribute("forms", amendmentForms);

    return "amendments/view-client";
  }

  @GetMapping("/amendClient1")
  public String viewAmendClient1(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    featureFlagsConfig.checkClaimAmendmentEnabled();

    var claim = getValidAmendableClaim(session, submissionId, claimId);
    var clientView = ClaimClientViewFactory.create(claim);
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("clientView", clientView);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    model.addAttribute("forms", amendmentForms);

    return "amendments/amend-client-1";
  }

  @PostMapping("/amendClient1")
  public String amendClient1(
      HttpSession session,
      @ModelAttribute("client1Form") AmendmentForm client1Form,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    featureFlagsConfig.checkClaimAmendmentEnabled();

    var amendmentForms = getAmendmentForms(session, claimId);

    // TODO: Validation can be done here and any errors returned

    amendmentForms.getClient1Form().setCurrent(client1Form);
    saveAmendmentForms(session, claimId, amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
