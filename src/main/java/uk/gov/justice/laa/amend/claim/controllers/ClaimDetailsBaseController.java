package uk.gov.justice.laa.amend.claim.controllers;

import static java.lang.Boolean.TRUE;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.ui.Model;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

public abstract class ClaimDetailsBaseController {

  protected void setCommonModelAttributes(
      Model model,
      HttpSession session,
      HttpServletRequest request,
      UUID submissionId,
      UUID claimId,
      ClaimDetails claim,
      FeatureFlagsConfig featureFlagsConfig) {
    String searchUrl = (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
    model.addAttribute("searchUrl", searchUrl);
    model.addAttribute("claimId", claimId);
    model.addAttribute("submissionId", submissionId);
    model.addAttribute("claim", claim.toViewModel());

    boolean isEscapedCase = claim.isEscapedCase();
    boolean isStageDisbursement =
        claim.isStageDisbursement()
            && TRUE.equals(featureFlagsConfig.getIsStageDisbursementEnabled());
    boolean isAssessmentButtonPresent =
        request.isUserInRole(ROLE_ESCAPE_CASE_CASEWORKER.name())
            && claim.isValid()
            && (isEscapedCase || isStageDisbursement);
    model.addAttribute("isAssessmentButtonPresent", isAssessmentButtonPresent);

    boolean isVoidButtonPresent =
        request.isUserInRole(ROLE_CLAIM_AMENDMENTS_CASEWORKER.name()) && claim.isValid();
    model.addAttribute("isVoidButtonPresent", isVoidButtonPresent);
  }
}
