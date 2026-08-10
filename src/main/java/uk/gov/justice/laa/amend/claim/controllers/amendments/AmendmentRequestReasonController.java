package uk.gov.justice.laa.amend.claim.controllers.amendments;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.forms.amendments.RequestedReasonForm;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getAmendmentForms;
import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.saveAmendmentForms;

@AllArgsConstructor
@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-reason")
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class AmendmentRequestReasonController {

  private final SystemReferenceService systemReferenceService;

  @ModelAttribute("amendmentReasonOptions")
  public Map<String, String> populateAmendmentReasons(HttpSession session, @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    var amendmentReasons = systemReferenceService.getAmendmentReasonByProvider(amendmentForms.getRequestedByForm().getRequestedBy());

    Map<String, String> codeToLabelMap = new LinkedHashMap<>();
    if (amendmentReasons != null && !amendmentReasons.isEmpty()) {
      codeToLabelMap =
              amendmentReasons.stream()
              .filter(
                  item -> item != null && item.getCode() != null && item.getDisplayLabel() != null)
              .collect(
                  Collectors.toMap(
                      AmendmentReasonReference::getCode,
                      AmendmentReasonReference::getDisplayLabel,
                      (existing, replacement) -> existing,
                      LinkedHashMap::new));
    }
    return codeToLabelMap;
  }

  @GetMapping()
  public String getAmendReasons(HttpSession session, Model model, @PathVariable UUID claimId, @PathVariable UUID submissionId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("requestedReasonForm", amendmentForms.getRequestedReasonForm());
    return "amendments/amend-request-reason";
  }

  @PostMapping
  public String postRequestedReason(HttpSession session, @ModelAttribute("requestedReasonForm") RequestedReasonForm form, @PathVariable UUID submissionId, @PathVariable UUID claimId) {
    var amendmentForms = getAmendmentForms(session, claimId);
    if (form.getRequestedReason() != null && !form.getRequestedReason().isEmpty()) {
      amendmentForms.getRequestedReasonForm().setRequestedReason(form.getRequestedReason());
      saveAmendmentForms(session, claimId, amendmentForms);
      return "redirect:/submissions/%s/claims/%s/amendments/client".formatted(submissionId, claimId);
    } else {
      return "amendments/amend-request-reason";
    }
  }
}
