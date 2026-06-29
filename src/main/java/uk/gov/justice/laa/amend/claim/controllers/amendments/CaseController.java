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
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.config.features.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@HasRoleClaimAmendmentsCaseworker
public class CaseController {

  @GetMapping("/case")
  @RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
  public String viewCase(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    var claimView = ClaimCaseViewFactory.create(claim);
    model.addAttribute("claim", claimView);

    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());
    model.addAttribute("originalCaseTypeForm", amendmentForms.getCaseTypeForm().getOriginal());
    model.addAttribute("forms", amendmentForms);

    return "amendments/view-case";
  }
}
