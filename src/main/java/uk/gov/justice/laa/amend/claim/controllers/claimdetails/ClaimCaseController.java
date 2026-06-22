package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.service.AssessmentService;
import uk.gov.justice.laa.amend.claim.service.UserRetrievalService;
import uk.gov.justice.laa.amend.claim.viewmodels.claimcase.ClaimCaseViewFactory;

@Controller
public class ClaimCaseController extends ClaimDetailsBaseController {

  public ClaimCaseController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      FeatureFlagsConfig featureFlagsConfig) {
    super(assessmentService, userRetrievalService, featureFlagsConfig);
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/case")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    featureFlagsConfig.checkFullClaimDetailsEnabled();

    var claim = getClaim(session, submissionId, claimId);

    var claimView = ClaimCaseViewFactory.create(claim);
    model.addAttribute("claim", claimView);

    var user = setLatestAssessment(claim);
    setCommonModelAttributes(model, session, request, claim, user);

    return "claimdetails/claim-case";
  }
}
