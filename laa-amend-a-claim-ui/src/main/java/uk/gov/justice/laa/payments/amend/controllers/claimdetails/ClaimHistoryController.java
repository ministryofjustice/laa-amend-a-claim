package uk.gov.justice.laa.payments.amend.controllers.claimdetails;

import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.service.ClaimHistoryService;
import uk.gov.justice.laa.amend.claim.viewmodels.history.ClaimHistoryEventViewModel;

@Controller
public class ClaimHistoryController extends ClaimDetailsBaseController {

  private final ClaimHistoryService claimHistoryService;
  private final MessageSource messageSource;

  public ClaimHistoryController(
      ClaimHistoryService claimHistoryService,
      FeatureFlagsConfig featureFlagsConfig,
      MessageSource messageSource) {
    super(featureFlagsConfig);
    this.claimHistoryService = claimHistoryService;
    this.messageSource = messageSource;
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
        claimHistory.lastUpdatedUser(),
        claimHistory.lastUpdatedDateTime());

    var locale = LocaleContextHolder.getLocale();
    var events =
        claimHistory.events().stream()
            .map(event -> ClaimHistoryEventViewModel.create(event, messageSource, locale))
            .toList();
    model.addAttribute("events", events);

    return "pages/claimdetails/claim-history";
  }
}
