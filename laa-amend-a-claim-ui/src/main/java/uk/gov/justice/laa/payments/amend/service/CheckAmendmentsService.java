package uk.gov.justice.laa.payments.amend.service;

import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_1;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_2;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch;
import uk.gov.justice.laa.payments.amend.client.ClaimsApiClient;
import uk.gov.justice.laa.payments.amend.exceptions.AmendmentSubmissionFailedException;
import uk.gov.justice.laa.payments.amend.forms.amendments.AmendmentForms;
import uk.gov.justice.laa.payments.amend.forms.amendments.OriginalAndCurrent;
import uk.gov.justice.laa.payments.amend.models.AmendmentError;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderView;

@Service
@Slf4j
public class CheckAmendmentsService {

  private static final String GENERIC_ERROR_MESSAGE =
      "A technical error occurred, please try again after some time";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final Set<HttpStatus> VALIDATION_REJECTION_STATUSES =
      Set.of(HttpStatus.BAD_REQUEST, HttpStatus.CONFLICT, HttpStatus.SERVICE_UNAVAILABLE);

  private final ClaimsApiClient claimsApiClient;
  private final Counter amendmentCounter;
  private final Counter amendmentRejectedCounter;
  private final Counter amendmentFailureCounter;

  public CheckAmendmentsService(ClaimsApiClient claimsApiClient, MeterRegistry meterRegistry) {
    this.claimsApiClient = claimsApiClient;
    this.amendmentCounter =
        Counter.builder("amendment.submissions.successful")
            .description("Total number of successful claim amendments")
            .register(meterRegistry);
    this.amendmentRejectedCounter =
        Counter.builder("amendment.submissions.rejected")
            .description("Total number of claim amendments rejected due to validation errors")
            .register(meterRegistry);
    this.amendmentFailureCounter =
        Counter.builder("amendment.submissions.failed")
            .description("Total number of claim amendments that failed unexpectedly")
            .register(meterRegistry);
  }

  public void submitAmendments(
      UUID submissionId,
      UUID claimId,
      UUID userId,
      ClaimDetails claim,
      AmendmentForms amendmentForms) {
    var patchBuilder =
        ClaimAmendmentPatch.builder()
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
      amendmentCounter.increment();
    } catch (WebClientResponseException ex) {
      if (!VALIDATION_REJECTION_STATUSES.contains(HttpStatus.resolve(ex.getStatusCode().value()))) {
        amendmentFailureCounter.increment();
        log.error(
            "Amendment submission to claims-api failed unexpectedly for submission {} claim {}"
                + " with status {}",
            submissionId,
            claimId,
            ex.getStatusCode(),
            ex);
        throw ex;
      }
      amendmentRejectedCounter.increment();
      log.warn(
          "Amendment submission rejected for submission {} claim {} with status {}: {}",
          submissionId,
          claimId,
          ex.getStatusCode(),
          ex.getResponseBodyAsString());
      throw new AmendmentSubmissionFailedException(submissionId, claimId, extractErrors(ex));
    } catch (Exception ex) {
      amendmentFailureCounter.increment();
      log.error("Failed to submit amendment for submission {} claim {}", submissionId, claimId, ex);
      throw ex;
    }
  }

  private List<AmendmentError> extractErrors(WebClientResponseException ex) {
    try {
      JsonNode root = OBJECT_MAPPER.readTree(ex.getResponseBodyAsString());
      JsonNode errors = root.path("errors");
      if (errors.isArray() && !errors.isEmpty()) {
        return errors.valueStream().map(CheckAmendmentsService::toAmendmentError).toList();
      }
      String detail = root.path("detail").asString(null);
      return List.of(AmendmentError.withMessage(detail != null ? detail : GENERIC_ERROR_MESSAGE));
    } catch (Exception parseException) {
      log.warn(
          "Failed to parse amendment error response body: {}",
          ex.getResponseBodyAsString(),
          parseException);
      return List.of(AmendmentError.withMessage(GENERIC_ERROR_MESSAGE));
    }
  }

  private static AmendmentError toAmendmentError(JsonNode error) {
    return new AmendmentError(
        error.path("code").asString(null),
        error.path("message").asString(GENERIC_ERROR_MESSAGE),
        error.path("fieldName").asString(null));
  }

  private void applyAmendments(
      ClaimAmendmentPatch.Builder builder, OriginalAndCurrent forms, ClaimDetails claim) {

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
      ClaimAmendmentPatch.Builder builder, OriginalAndCurrent forms) {
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
