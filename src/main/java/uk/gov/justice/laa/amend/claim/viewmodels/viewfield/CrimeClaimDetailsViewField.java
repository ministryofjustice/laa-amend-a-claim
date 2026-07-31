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
  REPRESENTATION_ORDER_DATE(
      new Accessor<>(CrimeClaimDetails::getRepresentationOrderDate), FieldType.DATE),
  STANDARD_FEE_CATEGORY(
      new Accessor<>(CrimeClaimDetails::getStandardFeeCategory),
      FieldOptions.STANDARD_FEE_CATEGORY),
  OUTCOME_FOR_CLIENT(new Accessor<>(CrimeClaimDetails::getOutcome), FieldOptions.OUTCOME),
  SUSPECTS_DEFENDANTS_COUNT(
      new Accessor<>(CrimeClaimDetails::getSuspectsDefendantsCount), FieldType.NUMBER),
  POLICE_STATION_COURT_ATTENDANCES_COUNT(
      new Accessor<>(CrimeClaimDetails::getPoliceStationCourtAttendancesCount), FieldType.NUMBER),
  POLICE_STATION_COURT_PRISON_ID(new Accessor<>(CrimeClaimDetails::getPoliceStationCourtPrisonId)),
  SCHEME_ID(new Accessor<>(CrimeClaimDetails::getSchemeId)),
  DSCC_NUMBER(new Accessor<>(CrimeClaimDetails::getDsccNumber)),
  MAAT_ID(new Accessor<>(CrimeClaimDetails::getMaatId)),
  PRISON_LAW_PRIOR_APPROVAL_NUMBER(
      new Accessor<>(CrimeClaimDetails::getPrisonLawPriorApprovalNumber)),
  IS_DUTY_SOLICITOR(new Accessor<>(CrimeClaimDetails::getIsDutySolicitor), FieldType.BOOLEAN),
  IS_YOUTH_COURT(new Accessor<>(CrimeClaimDetails::getIsYouthCourt), FieldType.BOOLEAN),

  // Cost fields
  TRAVEL_COSTS(new Accessor<>(CrimeClaimDetails::getTravelCosts), FieldType.BIG_DECIMAL),
  WAITING_COSTS(new Accessor<>(CrimeClaimDetails::getWaitingCosts), FieldType.BIG_DECIMAL);

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
