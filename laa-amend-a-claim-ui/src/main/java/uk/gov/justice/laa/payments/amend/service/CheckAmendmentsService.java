package uk.gov.justice.laa.payments.amend.service;

import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_1;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_2;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.OriginalAndCurrent;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckAmendmentsService {

  private final ClaimsApiClient claimsApiClient;

  public void submitAmendments(
      UUID submissionId,
      UUID claimId,
      UUID userId,
      ClaimDetails claim,
      AmendmentForms amendmentForms) {
    var patchBuilder =
        ClaimPatch.builder()
            .amendmentUserId(userId)
            .amendmentReasonCode(amendmentForms.getRequestedReasonForm().getRequestedReason())
            .amendmentRequestedBy(amendmentForms.getRequestedByForm().getRequestedBy())
            .version(claim.getVersion());

    applyAmendments(patchBuilder, amendmentForms.getClient1Form(), claim);
    if (amendmentForms.getClient2Form() != null) {
      applyAmendments(patchBuilder, amendmentForms.getClient2Form(), claim);
    }
    applyAmendments(patchBuilder, amendmentForms.getCaseTypeForm(), claim);
    if (claim instanceof CivilClaimDetails || claim instanceof MediationClaimDetails) {
      applyLegalHelpMatterTypeAmendments(patchBuilder, amendmentForms.getCaseTypeForm());
    }
    applyAmendments(patchBuilder, amendmentForms.getCaseDetailsForm(), claim);
    applyAmendments(patchBuilder, amendmentForms.getCostsForm(), claim);

    try {
      claimsApiClient.updateClaim(submissionId, claimId, patchBuilder.build()).block();
    } catch (WebClientResponseException ex) {
      // TODO: This will be handled gracefully by BC-651
      log.error(
          "Failed to submit amendment for submission {} claim {} with status {}: {}",
          submissionId,
          claimId,
          ex.getStatusCode(),
          ex.getResponseBodyAsString(),
          ex);
      throw ex;
    } catch (Exception ex) {
      log.error("Failed to submit amendment for submission {} claim {}", submissionId, claimId, ex);
      throw ex;
    }
  }

  private void applyAmendments(
      ClaimPatch.Builder builder, OriginalAndCurrent forms, ClaimDetails claim) {

    var original = forms.getOriginal();
    var current = forms.getCurrent();
    var claimIsAssessed = AmendmentsHeaderView.isAssessed(claim);

    for (var fieldValue : current.getFieldValues(claim.getClass()).entrySet()) {
      var field = fieldValue.getKey();
      if (field.isEditable(claimIsAssessed)
          && current.isAmendment(field.name(), original, field.getFieldType())) {
        field.applyPatch(builder, fieldValue.getValue());
      }
    }
  }

  /**
   * Matter type needs separate handling for legal help, as it is a concatenation of the two matter
   * types collected.
   */
  private void applyLegalHelpMatterTypeAmendments(
      ClaimPatch.Builder builder, OriginalAndCurrent forms) {
    var original = forms.getOriginal();
    var current = forms.getCurrent();

    if (!current.isAmendment(MATTER_TYPE_CODE_1.name(), original)
        && !current.isAmendment(MATTER_TYPE_CODE_2.name(), original)) {
      return;
    }

    var matterType1 = toStringOrEmpty(current.getAmendedValue(MATTER_TYPE_CODE_1.name()));
    var matterType2 = toStringOrEmpty(current.getAmendedValue(MATTER_TYPE_CODE_2.name()));

    builder.matterTypeCode("%s:%s".formatted(matterType1, matterType2));
  }

  private static String toStringOrEmpty(Object value) {
    return value == null ? "" : value.toString().trim();
  }
}
