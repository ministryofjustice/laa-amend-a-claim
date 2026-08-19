package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestedReasonFormValidator;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-reason")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@AllArgsConstructor
@Slf4j
public class AmendmentRequestedReasonController {

  private final RequestedReasonFormValidator requestedReasonFormValidator;
  private final SystemReferenceService systemReferenceService;

  @ModelAttribute("amendmentReasonOptions")
  public Map<String, String> populateAmendmentReasons(
      HttpSession session, @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    return systemReferenceService.getAmendmentRequestReason(
        amendmentForms.getRequestedByForm().getRequestedBy());
  }

  @GetMapping()
  public String getAmendReasons(
      HttpSession session,
      Model model,
      @PathVariable UUID claimId,
      @PathVariable UUID submissionId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    if (amendmentForms.getRequestedByForm() == null
        || amendmentForms.getRequestedByForm().getRequestedBy().isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "No requested by value found for submission %s claim %s"
              .formatted(submissionId, claimId));
    }
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("requestedReasonForm", amendmentForms.getRequestedReasonForm());
    return "amendments/amend-request-reason";
  }

  @PostMapping
  public String postRequestedReason(
      HttpSession session,
      @ModelAttribute("requestedReasonForm") RequestedReasonForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    // Manual invoke valida
    requestedReasonFormValidator.validate(
        form, bindingResult, amendmentForms.getRequestedByForm().getRequestedBy());

    if (bindingResult.hasErrors()) {
      return redirectWithErrors(
          redirectAttributes,
          bindingResult,
          "requestedReasonFormErrors",
          "/submissions/%s/claims/%s/amendments/requested-reason".formatted(submissionId, claimId));
    }

    amendmentForms.getRequestedReasonForm().setRequestedReason(form.getRequestedReason());
    saveAmendmentForms(session, claimId, amendmentForms);
    return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
  }
}
