package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;
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
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.exceptions.FeeCodeNotFoundException;
import uk.gov.justice.laa.amend.claim.factories.AvailableFeeCodesService;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class CaseTypeController {

  private final AvailableFeeCodesService availableFeeCodesService;

  @GetMapping("/amend-fee-code")
  public String amendFeeCode(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var availableFeeCodes = availableFeeCodesService.getAvailableFeeCodes(claim.getAreaOfLaw());
    if (!availableFeeCodes.containsKey(claim.getFeeCode())) {
      throw new FeeCodeNotFoundException(claim.getFeeCode());
    }

    var currentFeeCode = availableFeeCodes.get(claim.getFeeCode());
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("currentFeeCode", currentFeeCode);
    model.addAttribute("feeCodes", availableFeeCodes);
    model.addAttribute("feeCodeForm", amendmentForms.getCaseTypeForm().getCurrent());
    return "amendments/amend-fee-code";
  }

  @PostMapping("/amend-fee-code")
  public String postAmendFeeCode(
      HttpSession session,
      @ModelAttribute("feeCodeForm") AmendmentForm caseTypeForm,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);

    amendmentForms.getCaseTypeForm().setCurrent(caseTypeForm);
    saveAmendmentForms(session, claimId, amendmentForms);

    // TODO (BC-569): Redirect to matter type code page once implemented
    return "redirect:/submissions/%s/claims/%s/amendments/amend-matter-type"
        .formatted(submissionId, claimId);
  }
}
