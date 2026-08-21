package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getValidClaim;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForm;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.FieldSpecificAmendmentValidator;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.GenericAmendmentFieldValidator;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/amend-case-details")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
public class AmendCaseDetailsController extends AbstractAmendController {

  public AmendCaseDetailsController(
      List<GenericAmendmentFieldValidator> genericAmendmentFieldValidators,
      List<FieldSpecificAmendmentValidator> fieldSpecificAmendmentValidators) {
    super(genericAmendmentFieldValidators, fieldSpecificAmendmentValidators);
  }

  @InitBinder("caseDetailsForm")
  public void initCaseDetailsFormBinder(
      WebDataBinder binder,
      HttpSession session,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    initBinder(binder, session, submissionId, claimId);
  }

  @GetMapping
  public String amendCaseDetails(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    var claim = getValidClaim(session, submissionId, claimId);
    var caseView = ClaimCaseViewFactory.create(claim);

    model.addAttribute("caseView", caseView);
    model.addAttribute("caseDetailsForm", amendmentForms.getCaseDetailsForm().getCurrent());
    model.addAttribute("forms", amendmentForms);
    model.addAttribute("claimIsAssessed", AmendmentsHeaderView.isAssessed(claim));
    return "pages/amendments/amend-case-details";
  }

  @PostMapping
  public String postAmendCaseDetails(
      HttpSession session,
      @Valid @ModelAttribute("caseDetailsForm") AmendmentForm caseDetailsForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getValidClaim(session, submissionId, claimId);
    var amendmentForms = getAmendmentForms(session, claimId);
    var caseDetails = amendmentForms.getCaseDetailsForm();

    caseDetails.setCurrent(
        retainLockedInputs(
            caseDetailsForm,
            caseDetails.getOriginal(),
            lockedFields(ClaimCaseViewFactory.create(claim).caseDetailsRows().keySet(), claim)));
    saveAmendmentForms(session, claimId, amendmentForms);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "caseDetailsFormErrors",
          "/submissions/%s/claims/%s/amendments/amend-case-details"
              .formatted(submissionId, claimId));
    }

    return "redirect:/submissions/%s/claims/%s/amendments/case".formatted(submissionId, claimId);
  }
}
