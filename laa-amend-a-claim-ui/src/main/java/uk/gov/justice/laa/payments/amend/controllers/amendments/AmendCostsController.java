package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/amend-costs")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendCostsController extends AbstractAmendController {

  public AmendCostsController(
      List<GenericAmendmentFieldValidator> genericAmendmentFieldValidators,
      List<FieldSpecificAmendmentValidator> fieldSpecificAmendmentValidators) {
    super(genericAmendmentFieldValidators, fieldSpecificAmendmentValidators);
  }

  @InitBinder("costsForm")
  public void initClientFormBinder(
      WebDataBinder binder,
      HttpSession session,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    initBinder(binder, session, submissionId, claimId);
  }

  @GetMapping
  public String amendCosts(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    if (AmendmentsHeaderView.isAssessed(claim)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

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

    return "pages/amendments/amend-costs";
  }

  @PostMapping
  public String postAmendCosts(
      HttpSession session,
      @Valid @ModelAttribute("costsForm") AmendmentForm costsForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
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

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "costFormErrors",
          "/submissions/%s/claims/%s/amendments/amend-costs".formatted(submissionId, claimId));
    }

    return redirectToViewCosts(submissionId, claimId);
  }

  private static String redirectToViewCosts(UUID submissionId, UUID claimId) {
    return "redirect:/submissions/%s/claims/%s/amendments/costs".formatted(submissionId, claimId);
  }

  private static void retainEditableInputs(AmendmentForm costsForm, ClaimDetails claim) {
    var claimIsAssessed = AmendmentsHeaderView.isAssessed(claim);
    var editableFieldNames =
        ClaimCostsViewFactory.create(claim).costFields().keySet().stream()
            .filter(field -> field.isEditable(claimIsAssessed))
            .map(ClaimViewField::name)
            .toList();
    costsForm.getInputs().keySet().retainAll(editableFieldNames);
  }
}
