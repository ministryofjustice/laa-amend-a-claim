package uk.gov.justice.laa.amend.claim.controllers.amendments;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedByForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

@AllArgsConstructor
@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-by")
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class AmendmentRequestByController {

  private final SystemReferenceService systemReferenceService;

  @ModelAttribute("amendmentRequestByOptions")
  public Map<String, String> getAmendmentRequestByOptions() {
    var amendmentRequestedByReferenceList = systemReferenceService.getAmendmentRequestedByReferenceList();
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
  public String getRequestedBy(HttpSession session, Model model, @PathVariable UUID claimId, @PathVariable UUID submissionId) {

    var amendmentForms = getAmendmentForms(session, claimId);
    setModelAttributesForDisplay(model, claimId, submissionId, amendmentForms);
    return "amendments/amend-request-by";
  }

  private static void setModelAttributesForDisplay(Model model, UUID claimId, UUID submissionId, AmendmentForms amendmentForms) {
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("requestedByForm", amendmentForms.getRequestedByForm());
  }

  @PostMapping
  public String postRequestedBy(HttpSession session,
                                @ModelAttribute("requestedByForm") RequestedByForm form,
                                BindingResult bindingResult,
                                Model model, @PathVariable UUID submissionId, @PathVariable UUID claimId ) {
    var amendmentForms = getAmendmentForms(session, claimId);
    if (form.getRequestedBy() != null && !form.getRequestedBy().isEmpty()) {
      amendmentForms.getRequestedByForm().setRequestedBy(form.getRequestedBy());
      saveAmendmentForms(session, claimId, amendmentForms);
      return "redirect:/submissions/%s/claims/%s/amendments/requested-reason".formatted(submissionId, claimId);
    } else {
      bindingResult.rejectValue("requestedBy", "amendments.requestBy.required");
      setModelAttributesForDisplay(model, claimId, submissionId, amendmentForms);
      return "amendments/amend-request-by";
    }
  }
}
