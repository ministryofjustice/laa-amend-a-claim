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
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.factories.AvailableFeeCodesFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@HasRoleClaimAmendmentsCaseworker
@RequiredArgsConstructor
public class CaseTypeController {

  private final FeatureFlagsConfig featureFlagsConfig;
  private final AvailableFeeCodesFactory availableFeeCodesFactory;

  @GetMapping("/amend-fee-code")
  public String amendFeeCode(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    featureFlagsConfig.checkClaimAmendmentEnabled();

    var claim = getValidClaim(session, submissionId, claimId);
    var availableFeeCodes = availableFeeCodesFactory.getAvailableFeeCodes(claim.getAreaOfLaw());
    var currentFeeCode = availableFeeCodes.get(claim.getFeeCode());
    var amendmentForms = getAmendmentForms(session, claimId);

    model.addAttribute("currentFeeCode", currentFeeCode);
    model.addAttribute("feeCodes", availableFeeCodes);
    model.addAttribute("caseTypeForm", amendmentForms.getCaseTypeForm().getCurrent());
    return "amendments/amend-fee-code";
  }
}
