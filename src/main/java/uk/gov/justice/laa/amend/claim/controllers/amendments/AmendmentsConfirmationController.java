package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;

@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/confirmation")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@Controller
public class AmendmentsConfirmationController {

  @GetMapping
  public String confirmation(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    getValidClaim(session, submissionId, claimId);

    String searchUrl = (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");

    model.addAttribute("submissionId", submissionId);
    model.addAttribute("claimId", claimId);
    model.addAttribute("searchUrl", searchUrl);

    return "amendments/confirmation";
  }
}
