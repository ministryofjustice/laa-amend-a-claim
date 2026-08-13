package uk.gov.justice.laa.amend.claim.controllers.amendments;

import static uk.gov.justice.laa.amend.claim.utils.AmendmentFormRedirects.redirectWithErrors;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.amend.claim.config.features.Feature;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.forms.validators.RequestByFormValidator;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;

@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-by")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class AmendmentRequestByController {

  private final SystemReferenceService systemReferenceService;
  private final RequestByFormValidator requestByFormValidator;

  public AmendmentRequestByController(
      SystemReferenceService systemReferenceService,
      RequestByFormValidator requestByFormValidator) {
    this.systemReferenceService = systemReferenceService;
    this.requestByFormValidator = requestByFormValidator;
  }

  @InitBinder("requestedByForm")
  public void initRequestedByFormBinder(WebDataBinder binder) {
    binder.addValidators(requestByFormValidator);
  }

  private Map<String, String> getAmendmentRequestByOptions() {
    var amendmentRequestedByReferenceList =
        systemReferenceService.getAmendmentRequestedByReferenceList();
    Map<String, String> codeToLabelMap = new LinkedHashMap<>();
    if (amendmentRequestedByReferenceList != null
        && amendmentRequestedByReferenceList.getRequestedBy() != null) {
      codeToLabelMap =
          amendmentRequestedByReferenceList.getRequestedBy().stream()
              .filter(
                  item -> item != null && item.getCode() != null && item.getDisplayLabel() != null)
              .collect(
                  Collectors.toMap(
                      AmendmentRequestedByReference::getCode,
                      AmendmentRequestedByReference::getDisplayLabel,
                      (existing, replacement) -> existing,
                      LinkedHashMap::new));
    }
    return codeToLabelMap;
  }

  @GetMapping()
  public String getRequestedBy(
      HttpSession session,
      Model model,
      @PathVariable UUID claimId,
      @PathVariable UUID submissionId) {

    var amendmentForms = getAmendmentForms(session, claimId);
    setModelAttributesForDisplay(model, claimId, submissionId, amendmentForms);
    model.addAttribute("amendmentRequestByOptions", getAmendmentRequestByOptions());
    return "amendments/amend-request-by";
  }

  private void setModelAttributesForDisplay(
      Model model, UUID claimId, UUID submissionId, AmendmentForms amendmentForms) {
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("requestedByForm", amendmentForms.getRequestedByForm());
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
          "requestedByFormErrors",
          "/submissions/%s/claims/%s/amendments/requested-by".formatted(submissionId, claimId));
    }
    amendmentForms.getRequestedByForm().setRequestedBy(form.getRequestedBy());
    saveAmendmentForms(session, claimId, amendmentForms);
    return "redirect:/submissions/%s/claims/%s/amendments/requested-reason"
        .formatted(submissionId, claimId);
  }
}
