package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static uk.gov.justice.laa.payments.amend.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.payments.amend.forms.validators.RequestedByFormValidator;
import uk.gov.justice.laa.payments.amend.service.SystemReferenceService;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-by")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@RequiredArgsConstructor
@Slf4j
public class AmendmentRequestedByController {

  private final SystemReferenceService systemReferenceService;
  private final RequestedByFormValidator requestedByFormValidator;

  @InitBinder("requestedByForm")
  public void initRequestedByFormBinder(WebDataBinder binder) {
    binder.addValidators(requestedByFormValidator);
  }

  @GetMapping()
  public String getRequestedBy(
      HttpSession session,
      Model model,
      @PathVariable UUID claimId,
      @PathVariable UUID submissionId) {

    var amendmentForms = getAmendmentForms(session, claimId);

    setModelAttributesForDisplay(model, claimId, submissionId, amendmentForms);
    model.addAttribute(
        "amendmentRequestedByOptions", systemReferenceService.getAmendmentRequestedByOptions());
    return "pages/amendments/amend-requested-by";
  }

  @PostMapping
  public String postRequestedBy(
      HttpSession session,
      @Valid @ModelAttribute("requestedByForm") RequestedByForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "/submissions/%s/claims/%s/amendments/requested-by".formatted(submissionId, claimId));
    }
    amendmentForms.getRequestedByForm().setRequestedBy(form.getRequestedBy());
    saveAmendmentForms(session, claimId, amendmentForms);
    return "redirect:/submissions/%s/claims/%s/amendments/requested-reason"
        .formatted(submissionId, claimId);
  }

  private void setModelAttributesForDisplay(
      Model model, UUID claimId, UUID submissionId, AmendmentForms amendmentForms) {
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("requestedByForm", amendmentForms.getRequestedByForm());
  }
}
