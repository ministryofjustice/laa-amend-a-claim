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
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.payments.amend.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
public class ClaimClientController extends ClaimDetailsBaseController {

  private final ClaimHistoryService claimHistoryService;

  public ClaimClientController(
      ClaimHistoryService claimHistoryService, FeatureFlagsConfig featureFlagsConfig) {
    super(featureFlagsConfig);
    this.claimHistoryService = claimHistoryService;
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

    var history = claimHistoryService.getClaimHistorySummary(claim);
    model.addAttribute("amendedFields", history.amendedFields());

    setCommonModelAttributes(
        model, session, request, claim, history.lastUpdatedUser(), history.lastUpdatedDateTime());

    return "pages/claimdetails/claim-client";
  }
}
