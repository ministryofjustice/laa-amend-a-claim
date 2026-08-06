package uk.gov.justice.laa.amend.claim.controllers.amendments;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.justice.laa.amend.claim.exceptions.NoClaimInSessionException;
import uk.gov.justice.laa.amend.claim.utils.SessionUtils;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderView;
import uk.gov.justice.laa.amend.claim.viewmodels.AmendmentsHeaderViewFactory;

@ControllerAdvice(basePackageClasses = AmendmentsHeaderAdvice.class)
@RequiredArgsConstructor
public class AmendmentsHeaderAdvice {

  private final AmendmentsHeaderViewFactory amendmentsHeaderViewFactory;

  @ModelAttribute("amendmentsHeaderView")
  public AmendmentsHeaderView amendmentsHeaderView(
      HttpServletRequest request,
      HttpSession session,
      @PathVariable(required = false) UUID submissionId,
      @PathVariable(required = false) UUID claimId) {
    if (!"GET".equalsIgnoreCase(request.getMethod()) || submissionId == null || claimId == null) {
      return null;
    }
    try {
      var claim = SessionUtils.getClaim(session, submissionId, claimId);
      return amendmentsHeaderViewFactory.create(claim);
    } catch (NoClaimInSessionException e) {
      return null;
    }
  }
}
