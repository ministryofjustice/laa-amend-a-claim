package uk.gov.justice.laa.amend.claim.controllers.amendments;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AmendmentRequestedByReference;

@AllArgsConstructor
@Controller
@RequestMapping("/amendments")
@HasRoleClaimAmendmentsCaseworker
@Slf4j
public class AmendmentRequestByController {

  private final ClaimService claimService;

  @GetMapping("/requested-by")
  public String getRequestedBy(Model model) {

    var amendmentRequestedByReferenceList = claimService.getAmendmentRequestedByReferenceList();
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
    model.addAttribute("amendmentRequestByOptions", codeToLabelMap);

    return "amendments/amend-request-by";
  }
}
