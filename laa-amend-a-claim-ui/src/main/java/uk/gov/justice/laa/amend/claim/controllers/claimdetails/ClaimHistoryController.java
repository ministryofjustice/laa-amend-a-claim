package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.service.ClaimHistoryService;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimHistoryEventViewModel;

@Controller
public class ClaimHistoryController extends ClaimDetailsBaseController {

  private final ClaimHistoryService claimHistoryService;

  public ClaimHistoryController(
      ClaimHistoryService claimHistoryService, FeatureFlagsConfig featureFlagsConfig) {
    super(featureFlagsConfig);
    this.claimHistoryService = claimHistoryService;
  }

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/history")
  public String onPageLoad(
      HttpServletRequest request,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId)
      throws IOException {
    var claim = getClaim(session, submissionId, claimId);
    var claimHistory = claimHistoryService.getClaimHistory(claim);

    setCommonModelAttributes(
        model,
        session,
        request,
        claim,
        claimHistory.latestAssessmentUser().orElse(null),
        claim.getLastUpdatedDateTime()); // TODO: BC-570: Will wire up user and time from history

    var events = claimHistory.events().stream().map(ClaimHistoryEventViewModel::create).toList();
    model.addAttribute("events", events);

    return "pages/claimdetails/claim-history";
  }
}
