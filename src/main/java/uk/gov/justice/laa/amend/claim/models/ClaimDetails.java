package uk.gov.justice.laa.amend.claim.models;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_REASON_ESCAPE_CASE_CONTINGENCY;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.ASSESSMENT_REASON_STAGE_DISBURSEMENT_CONTINGENCY;
import static uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.STAGE_DISBURSEMENT_FEE_CODES;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.justice.laa.amend.claim.mappers.AssessmentMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.AssessmentPost;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ClaimDetails extends Claim {
  private String categoryOfLaw;
  private String matterTypeCode;
  private String providerName;
  private Boolean vatApplicable;
  private ClaimField vatClaimed;
  private ClaimField fixedFee;
  private ClaimField netProfitCost;
  private ClaimField netDisbursementAmount;
  private ClaimField totalAmount;
  private ClaimField disbursementVatAmount;

  private ClaimField assessedTotalVat;
  private ClaimField assessedTotalInclVat;
  private ClaimField allowedTotalVat;
  private ClaimField allowedTotalInclVat;

  private OutcomeType assessmentOutcome;
  private String assessmentReason;
  private OffsetDateTime submittedDate;
  private String feeCode;
  private String feeCodeDescription;
  private boolean hasAssessment;
  private AssessmentInfo lastAssessment;
  private String lastUpdatedUser;
  private OffsetDateTime lastUpdatedDateTime;

  public void applyOutcome(OutcomeType outcome) {
    getClaimFields().forEach(x -> x.applyOutcome(outcome));
  }

  public abstract boolean isAssessedTotalFieldAssessable();

  public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();

  public abstract AssessmentPost toAssessment(AssessmentMapper mapper, String userId);

  @JsonIgnore
  public Stream<@NotNull ClaimField> getClaimFields() {
    return Stream.concat(commonClaimFields(), specificClaimFields()).filter(Objects::nonNull);
  }

  protected Stream<ClaimField> commonClaimFields() {
    return Stream.of(
            Stream.of(
                getVatClaimed(),
                getFixedFee(),
                getNetProfitCost(),
                getNetDisbursementAmount(),
                getDisbursementVatAmount(),
                getTotalAmount()),
            getAssessedTotalFields(),
            getAllowedTotalFields())
        .flatMap(Function.identity());
  }

  protected abstract Stream<ClaimField> specificClaimFields();

  @JsonIgnore
  public Stream<ClaimField> getAssessedTotalFields() {
    return Stream.of(getAssessedTotalVat(), getAssessedTotalInclVat());
  }

  @JsonIgnore
  public Stream<ClaimField> getAllowedTotalFields() {
    return Stream.of(getAllowedTotalVat(), getAllowedTotalInclVat());
  }

  public boolean isEscapedCase() {
    return Optional.ofNullable(getEscaped()).orElse(false);
  }

  public boolean isStageDisbursement() {
    return nonNull(feeCode) && STAGE_DISBURSEMENT_FEE_CODES.contains(feeCode);
  }

  public boolean isContingencyAssessment() {
    return ASSESSMENT_REASON_ESCAPE_CASE_CONTINGENCY.equals(assessmentReason)
        || ASSESSMENT_REASON_STAGE_DISBURSEMENT_CONTINGENCY.equals(assessmentReason);
  }
}
