package uk.gov.justice.laa.amend.claim.handlers;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;
import uk.gov.justice.laa.amend.claim.forms.AmendClientForm;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientView;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;

@Slf4j
@Component
public class ClaimClientHandler {
  public ClaimClientView loadClaim(ExternalContext context, UUID submissionId, UUID claimId) {
    var claim = getClaim(context.getSessionMap(), submissionId, claimId);
    return ClaimClientViewFactory.create(claim);
  }

  public AmendClientForm populateForm(ClaimClientView claimClientView) {
    return new AmendClientForm(claimClientView);
  }

  // TODO: Have a separate form for each area of law and a separate save method for each
  public void save(AmendClientForm form) {
    Map<String, String> inputs = form.getInputs();

    Map<String, Object> outputs = new HashMap<>();
  }
}
