package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_1;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_2;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.amend.claim.client.ClaimsApiClient;
import uk.gov.justice.laa.amend.claim.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.amend.claim.forms.amendments.OriginalAndCurrent;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmendmentsCheckService {

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
            // TODO: BC-574 will cover these values
            .amendmentReasonCode("CASE_REOPENED_REBILLED")
            .amendmentRequestedBy("PROVIDER")
            .version(claim.getVersion());

    applyAmendments(patchBuilder, amendmentForms.getClient1Form(), claim.getClass());
    if (amendmentForms.getClient2Form() != null) {
      applyAmendments(patchBuilder, amendmentForms.getClient2Form(), claim.getClass());
    }
    applyAmendments(patchBuilder, amendmentForms.getCaseTypeForm(), claim.getClass());
    if (claim instanceof CivilClaimDetails) {
      applyLegalHelpMatterTypeAmendments(patchBuilder, amendmentForms.getCaseTypeForm());
    }
    applyAmendments(patchBuilder, amendmentForms.getCaseDetailsForm(), claim.getClass());

    try {
      claimsApiClient.updateClaim(submissionId, claimId, patchBuilder.build()).block();
    } catch (WebClientResponseException ex) {
      // TODO: This will be handled gracefully by BC-651
      log.error(
          "Failed to submit amendment for submission {} claim {}: {}",
          submissionId,
          claimId,
          ex.getResponseBodyAsString(),
          ex);
      throw ex;
    } catch (Exception ex) {
      log.error("Failed to submit amendment for submission {} claim {}", submissionId, claimId, ex);
      throw ex;
    }
  }

  private void applyAmendments(
      ClaimPatch.Builder builder,
      OriginalAndCurrent forms,
      Class<? extends ClaimDetails> claimDetailsType) {

    var original = forms.getOriginal();
    var current = forms.getCurrent();

    for (var fieldValue : current.getFieldValues(claimDetailsType).entrySet()) {
      var field = fieldValue.getKey();
      if (!current.isAmendment(field.name(), original)) {
        continue;
      }
      field.applyPatch(builder, fieldValue.getValue());
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
