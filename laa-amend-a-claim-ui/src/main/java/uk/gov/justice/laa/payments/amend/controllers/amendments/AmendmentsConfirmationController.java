package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VALID;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getValidClaim;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.justice.laa.payments.amend.annotations.HasRoleClaimAmendmentsCaseworker;
import uk.gov.justice.laa.payments.amend.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.payments.amend.config.features.Feature;
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.payments.amend.service.ClaimService;
import uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsViewFactory;

@RequestMapping("/submissions/{submissionId}/claims/{claimId}/amendments/confirmation")
@RequiresFeatureFlag(Feature.CLAIM_AMENDMENT)
@HasRoleClaimAmendmentsCaseworker
@RequiredArgsConstructor
@Controller
public class AmendmentsConfirmationController {

  private final ClaimService claimService;
  private final ClaimHistoryService claimHistoryService;

  @GetMapping
  public String confirmation(
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    getValidClaim(session, submissionId, claimId);
    var claim = claimService.getClaimDetails(submissionId, claimId);

    if (!claim.isAmended() || claim.getStatus() != VALID) {
      throw new ResponseStatusException(NOT_FOUND, "Claim is not amended or not valid");
    }

    var confirmation = claimHistoryService.getAmendmentConfirmation(claim);

    String searchUrl = (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");

    model.addAttribute("submissionId", submissionId);
    model.addAttribute("claimId", claimId);
    model.addAttribute("searchUrl", searchUrl);
    model.addAttribute("confirmation", confirmation);

    if (confirmation.hasCalculatedCostsChanged()) {
      var claimView = ClaimCostsViewFactory.create(claim);
      model.addAttribute("claim", claimView);
      model.addAttribute("updatedClaimTotal", claim.getTotalAmount().getCalculated());
    }

    return "pages/amendments/confirmation";
  }
}
