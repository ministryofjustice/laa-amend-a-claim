package uk.gov.justice.laa.amend.claim.handlers;

import static uk.gov.justice.laa.amend.claim.utils.SessionUtils.getClaim;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;
import uk.gov.justice.laa.amend.claim.forms.amendment.AmendForm;
import uk.gov.justice.laa.amend.claim.forms.amendment.AmendFormFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientView;
import uk.gov.justice.laa.amend.claim.viewmodels.claimclient.ClaimClientViewFactory;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

@Slf4j
@Component
public class ClaimClientHandler {
  public ClaimClientView<?> loadClaim(ExternalContext context, UUID submissionId, UUID claimId) {
    var claim = getClaim(context.getSessionMap(), submissionId, claimId);
    return ClaimClientViewFactory.create(claim);
  }

  public AmendForm populateClient1Form(ClaimClientView<?> claimClientView) {
    return AmendFormFactory.create(
        claimClientView.client1Rows(), claimClientView.claimDetailsType());
  }

  public AmendForm populateClient2Form(ClaimClientView<?> claimClientView) {
    return AmendFormFactory.create(
        claimClientView.client2Rows(), claimClientView.claimDetailsType());
  }

  public void save(AmendForm form) {
    Map<ClaimViewField<?>, String> inputs = form.getFieldInputs();

    Map<String, Object> outputs = new HashMap<>();
  }
}
