package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcase.ClaimCaseViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendCaseTabController {

  @GetMapping("/case")
  public String viewCase(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);

    var claimView = ClaimCaseViewFactory.create(claim);
    model.addAttribute("claim", claimView);
    model.addAttribute("forms", amendmentForms);
    model.addAttribute("caseTypeAmendUrl", caseTypeAmendUrl(claim, submissionId, claimId));

    return "pages/amendments/view-case";
  }

  private static String caseTypeAmendUrl(ClaimDetails claim, UUID submissionId, UUID claimId) {
    var page = caseTypeAmendPage(claim);
    return "/submissions/%s/claims/%s/amendments/%s".formatted(submissionId, claimId, page);
  }

  private static String caseTypeAmendPage(ClaimDetails claim) {
    if (ClaimDetailsViewField.FEE_CODE.isEditable(AmendmentsHeaderView.isAssessed(claim))) {
      return "amend-fee-code";
    }
    return claim.getAreaOfLaw() == AreaOfLaw.CRIME_LOWER
        ? "amend-stage-reached"
        : "amend-matter-type";
  }
}
