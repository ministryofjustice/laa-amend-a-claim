package uk.gov.justice.laa.amend.claim.controllers.amendments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.service.SystemReferenceService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentReasonReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/requested-reason")
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class AmendmentRequestReasonController {

  private final SystemReferenceService systemReferenceService;

  @GetMapping()
  public String getAmendReasons(Model model, @PathVariable String claimId, @PathVariable String submissionId) {
    var amendmentReasons = systemReferenceService.getAmendmentReasonByProvider("Assurance");
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
    model.addAttribute("amendmentReasonOptions", codeToLabelMap);
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);

    return "amendments/amend-request-reason";
  }

  @PostMapping
  public String postRequestedBy() {
    return "redirect:/submissions/{submissionId}/claims/{claimId}/amendments/case";
  }
}
