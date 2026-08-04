package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendClientTabController {

  @GetMapping("/client")
  public String viewClient(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var clientView = ClaimClientViewFactory.create(claim);
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("areaOfLaw", claim.getAreaOfLaw());
    model.addAttribute("clientView", clientView);
    model.addAttribute("client1Form", amendmentForms.getClient1Form().getCurrent());
    if (AreaOfLaw.MEDIATION.equals(claim.getAreaOfLaw())) {
      model.addAttribute("client2Form", amendmentForms.getClient2Form().getCurrent());
    }
    model.addAttribute("forms", amendmentForms);

    return "redirect:/submissions/%s/claims/%s/amendments/requested-by"
        .formatted(submissionId, claimId);
  }
}
