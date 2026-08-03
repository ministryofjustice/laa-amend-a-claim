package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;

@Getter
public enum CrimeClaimDetailsViewField implements ClaimViewField<CrimeClaimDetails> {
  // Case type fields
  FEE_CODE(FieldType.TEXT, String.class, CrimeClaimDetails::getFeeCode, Builder::feeCode),
  MATTER_TYPE_CODE(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getMatterTypeCode,
      Builder::crimeMatterTypeCode),

  // Case fields
  STAGE_REACHED(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getStageReached,
      Builder::stageReachedCode,
      FieldOptions.CRIME_STAGE_REACHED),
  REPRESENTATION_ORDER_DATE(
      FieldType.DATE,
      String.class,
      CrimeClaimDetails::getRepresentationOrderDate,
      Builder::representationOrderDate),
  STANDARD_FEE_CATEGORY(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getStandardFeeCategory,
      Builder::standardFeeCategoryCode,
      FieldOptions.STANDARD_FEE_CATEGORY),
  OUTCOME_FOR_CLIENT(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.OUTCOME),
  SUSPECTS_DEFENDANTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getSuspectsDefendantsCount,
      Builder::suspectsDefendantsCount),
  POLICE_STATION_COURT_ATTENDANCES_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getPoliceStationCourtAttendancesCount,
      Builder::policeStationCourtAttendancesCount),
  POLICE_STATION_COURT_PRISON_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPoliceStationCourtPrisonId,
      Builder::policeStationCourtPrisonId),
  SCHEME_ID(FieldType.TEXT, String.class, CrimeClaimDetails::getSchemeId, Builder::schemeId),
  DSCC_NUMBER(FieldType.TEXT, String.class, CrimeClaimDetails::getDsccNumber, Builder::dsccNumber),
  MAAT_ID(FieldType.TEXT, String.class, CrimeClaimDetails::getMaatId, Builder::maatId),
  PRISON_LAW_PRIOR_APPROVAL_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPrisonLawPriorApprovalNumber,
      Builder::prisonLawPriorApprovalNumber),
  IS_DUTY_SOLICITOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CrimeClaimDetails::getIsDutySolicitor,
      Builder::isDutySolicitor),
  IS_YOUTH_COURT(
      FieldType.BOOLEAN, Boolean.class, CrimeClaimDetails::getIsYouthCourt, Builder::isYouthCourt),

  // Cost fields
  TRAVEL_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getTravelCosts,
      Builder::travelWaitingCostsAmount),
  WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getWaitingCosts,
      Builder::netWaitingCostsAmount);

  private final CrimeClaimViewFieldGetter<?> getter;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher) {
    this(fieldType, patchType, getter, patcher, List.of());
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options) {
    this.getter = new CrimeClaimViewFieldGetter<>(getter);
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
  }

  public record CrimeClaimViewFieldGetter<T>(Function<CrimeClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CrimeClaimDetails, T> {}
}
