package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;

@Getter
public enum CrimeClaimDetailsViewField implements ClaimViewField<CrimeClaimDetails> {
  // Case type fields
  FEE_CODE(new Accessor<>(CrimeClaimDetails::getFeeCode)),
  MATTER_TYPE_CODE(new Accessor<>(CrimeClaimDetails::getMatterTypeCode)),

  // Case fields
  STAGE_REACHED(new Accessor<>(CrimeClaimDetails::getStageReached), FieldOptions.STAGE_REACHED),
  UNIQUE_FILE_NUMBER(new Accessor<>(CrimeClaimDetails::getUniqueFileNumber)),
  REPRESENTATION_ORDER_DATE(
      new Accessor<>(CrimeClaimDetails::getRepresentationOrderDate), FieldType.DATE),
  CASE_CONCLUDED_DATE(new Accessor<>(CrimeClaimDetails::getCaseEndDate), FieldType.DATE),
  STANDARD_FEE_CATEGORY(
      new Accessor<>(CrimeClaimDetails::getStandardFeeCategory),
      FieldOptions.STANDARD_FEE_CATEGORY),
  OUTCOME_FOR_CLIENT(new Accessor<>(CrimeClaimDetails::getOutcome), FieldOptions.OUTCOME),
  SUSPECTS_DEFENDANTS_COUNT(new Accessor<>(CrimeClaimDetails::getSuspectsDefendantsCount)),
  POLICE_STATION_COURT_ATTENDANCES_COUNT(
      new Accessor<>(CrimeClaimDetails::getPoliceStationCourtAttendancesCount)),
  POLICE_STATION_COURT_PRISON_ID(new Accessor<>(CrimeClaimDetails::getPoliceStationCourtPrisonId)),
  SCHEME_ID(new Accessor<>(CrimeClaimDetails::getSchemeId)),
  DSCC_NUMBER(new Accessor<>(CrimeClaimDetails::getDsccNumber)),
  MAAT_ID(new Accessor<>(CrimeClaimDetails::getMaatId)),
  PRISON_LAW_PRIOR_APPROVAL_NUMBER(
      new Accessor<>(CrimeClaimDetails::getPrisonLawPriorApprovalNumber)),
  IS_DUTY_SOLICITOR(new Accessor<>(CrimeClaimDetails::getIsDutySolicitor), FieldType.BOOLEAN),
  IS_YOUTH_COURT(new Accessor<>(CrimeClaimDetails::getIsYouthCourt), FieldType.BOOLEAN);

  private final Accessor<?> accessor;
  private final FieldType type;
  private final List<FieldOption> options;

  CrimeClaimDetailsViewField(Accessor<?> accessor) {
    this(accessor, FieldType.TEXT);
  }

  CrimeClaimDetailsViewField(Accessor<?> accessor, FieldType type) {
    this(accessor, type, List.of());
  }

  CrimeClaimDetailsViewField(Accessor<?> accessor, List<FieldOption> options) {
    this(accessor, FieldType.ENUM, options);
  }

  CrimeClaimDetailsViewField(Accessor<?> accessor, FieldType type, List<FieldOption> options) {
    this.accessor = accessor;
    this.type = type;
    this.options = List.copyOf(options);
  }

  public record Accessor<T>(Function<CrimeClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<CrimeClaimDetails, T> {}
}
