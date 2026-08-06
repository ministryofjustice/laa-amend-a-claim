package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

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
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcosts.ClaimCostsViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments")
@RequiredArgsConstructor
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class CostsController {

  @GetMapping("/costs")
  public String viewCosts(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    addCostsModelAttributes(session, model, submissionId, claimId);

    return "amendments/view-costs";
  }

  @GetMapping("/amend-costs")
  public String amendCosts(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (AmendmentsHeaderView.isAssessed(claim)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    addCostsModelAttributes(session, model, submissionId, claimId);

    return "amendments/amend-costs";
  }

  @PostMapping("/amend-costs")
  public String postAmendCosts(
      HttpSession session,
      @ModelAttribute("costsForm") AmendmentForm costsForm,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (AmendmentsHeaderView.isAssessed(claim)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    retainEditableInputs(costsForm, claim);

    var amendmentForms = getAmendmentForms(session, claimId);
    amendmentForms.getCostsForm().setCurrent(costsForm);
    saveAmendmentForms(session, claimId, amendmentForms);

    return redirectToViewCosts(submissionId, claimId);
  }

  private static String redirectToViewCosts(UUID submissionId, UUID claimId) {
    return "redirect:/submissions/%s/claims/%s/amendments/costs".formatted(submissionId, claimId);
  }

  private static void retainEditableInputs(AmendmentForm costsForm, ClaimDetails claim) {
    var editableFieldNames =
        ClaimCostsViewFactory.create(claim).costFields().keySet().stream()
            .filter(field -> field.isEditable())
            .map(field -> field.name())
            .toList();
    costsForm.getInputs().keySet().retainAll(editableFieldNames);
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
    model.addAttribute("forms", amendmentForms);
  }
}
