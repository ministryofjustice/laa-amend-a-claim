package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_CLAIM_AMENDMENTS_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.ui.Model;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.InquestConfig;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MicrosoftApiUser;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.amend.claim.utils.InquestEligibility;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsFooterView;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsHeaderView;

public abstract class ClaimDetailsBaseController {

  private final AssessmentService assessmentService;
  private final UserRetrievalService userRetrievalService;
  protected final FeatureFlagsConfig featureFlagsConfig;
  protected final InquestConfig inquestConfig;

  public ClaimDetailsBaseController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      FeatureFlagsConfig featureFlagsConfig,
      InquestConfig inquestConfig) {
    this.assessmentService = assessmentService;
    this.userRetrievalService = userRetrievalService;
    this.featureFlagsConfig = featureFlagsConfig;
    this.inquestConfig = inquestConfig;
  }

  protected void setCommonModelAttributes(
      Model model,
      HttpSession session,
      HttpServletRequest request,
      ClaimDetails claim,
      MicrosoftApiUser user) {
    String searchUrl = (String) Optional.ofNullable(session.getAttribute("searchUrl")).orElse("/");
    model.addAttribute("searchUrl", searchUrl);
    model.addAttribute("claimId", claim.getClaimId());
    model.addAttribute("submissionId", claim.getSubmissionId());
    model.addAttribute("isInquestEligible", isInquestEligible(claim));

    model.addAttribute("claimDetailsHeaderView", new ClaimDetailsHeaderView(claim, user));

    model.addAttribute(
        "claimDetailsFooterView",
        new ClaimDetailsFooterView(
            claim,
            request.isUserInRole(ROLE_ESCAPE_CASE_CASEWORKER.name()),
            request.isUserInRole(ROLE_CLAIM_AMENDMENTS_CASEWORKER.name()),
            featureFlagsConfig));
  }

  protected boolean isInquestEligible(ClaimDetails claim) {
    return InquestEligibility.isEligible(claim, inquestConfig.getMatterTypeCodes());
  }

  protected MicrosoftApiUser setLatestAssessment(ClaimDetails claim) {
    if (claim.isHasAssessment()) {
      claim = assessmentService.getLatestAssessmentByClaim(claim);
      if (claim.getLastUpdatedUser() != null) {
        return userRetrievalService.getUser(claim.getLastUpdatedUser());
      }
    }
    return null;
  }
}
