package uk.gov.justice.laa.amend.claim.models;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
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
    private String scheduleReference;
    private String providerName;
    private Boolean escaped;
    private Boolean vatApplicable;
    private String providerAccountNumber;
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
    private LocalDateTime submittedDate;
    private String feeCode;
    private String feeCodeDescription;
    private boolean hasAssessment;
    private AssessmentInfo lastAssessment;

    public void applyOutcome(OutcomeType outcome) {
        getClaimFields().forEach(x -> x.applyOutcome(outcome));
    }

    public abstract boolean isAssessedTotalFieldAssessable();

    public abstract ClaimDetailsView<? extends ClaimDetails> toViewModel();

    public abstract AssessmentPost toAssessment(AssessmentMapper mapper, String userId);

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

    public Stream<ClaimField> getAssessedTotalFields() {
        return Stream.of(getAssessedTotalVat(), getAssessedTotalInclVat());
    }

    public Stream<ClaimField> getAllowedTotalFields() {
        return Stream.of(getAllowedTotalVat(), getAllowedTotalInclVat());
    }
}
