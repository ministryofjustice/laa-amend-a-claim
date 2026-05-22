package uk.gov.justice.laa.amend.claim.controllers.claimdetails;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Controller
@RequiredArgsConstructor
public class ClaimClientController extends ClaimDetailsBaseController {

  private final FeatureFlagsConfig featureFlagsConfig;

  @GetMapping("/submissions/{submissionId}/claims/{claimId}/client")
  public String onPageLoad(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session,
      Model model,
      @PathVariable UUID submissionId,
      @PathVariable UUID claimId)
      throws IOException {

    if (!featureFlagsConfig.getIsFullClaimDetailsEnabled()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    var claim = getClaim(session, submissionId, claimId);

    var claimView = ClaimClientViewFactory.create(claim);
    model.addAttribute("claim", claimView);

    // TODO: Populate user
    setCommonModelAttributes(model, session, request, claim, null);

    return "claimdetails/claim-client";
  }
}
