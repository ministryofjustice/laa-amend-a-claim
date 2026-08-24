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
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/costs")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendCostsTabController {

  @GetMapping
  public String viewCosts(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    addCostsModelAttributes(session, model, submissionId, claimId);

    return "pages/amendments/view-costs";
  }

  private void addCostsModelAttributes(
      HttpSession session, Model model, UUID submissionId, UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var costFields = ClaimCostsViewFactory.create(claim).costFields();
    var amendmentForms = getAmendmentForms(session, claimId);
    var costs = amendmentForms.getCostsForm();
    var costsFormIsAmended =
        costFields.keySet().stream()
            .anyMatch(
                field ->
                    costs
                        .getCurrent()
                        .isAmendment(field.name(), costs.getOriginal(), field.getFieldType()));

    model.addAttribute("costFields", costFields);
    model.addAttribute("costsForm", costs.getCurrent());
    model.addAttribute("costsFormIsAmended", costsFormIsAmended);
    model.addAttribute("claimIsAssessed", AmendmentsHeaderView.isAssessed(claim));
    model.addAttribute("forms", amendmentForms);
  }
}
