package uk.gov.justice.laa.payments.amend.controllers.claimdetails;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.service.AssessmentService;
import uk.gov.justice.laa.payments.amend.service.UserRetrievalService;
import uk.gov.justice.laa.payments.amend.utils.SessionUtils;
import uk.gov.justice.laa.payments.amend.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
public class ClaimClientController extends ClaimDetailsBaseController {

  public ClaimClientController(
      AssessmentService assessmentService,
      UserRetrievalService userRetrievalService,
      FeatureFlagsConfig featureFlagsConfig) {
    super(assessmentService, userRetrievalService, featureFlagsConfig);
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/client")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId) {
    var claim = getClaim(session, submissionId, claimId);

    var claimView = ClaimClientViewFactory.create(claim);
    model.addAttribute("claim", claimView);

    var amendedFields = SessionUtils.getAmendedFields(session, claimId);
    model.addAttribute("amendedFields", amendedFields);

    var user = setLatestAssessment(claim);
    setCommonModelAttributes(model, session, request, claim, user);

    return "pages/claimdetails/claim-client";
  }
}
