package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.Amendability;
import uk.gov.justice.laa.amend.claim.models.enums.FieldType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Getter
public enum CrimeClaimDetailsViewField implements ClaimViewField<CrimeClaimDetails> {
  // Case type fields
  MATTER_TYPE_CODE(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getMatterTypeCode,
      ClaimPatch.Builder::crimeMatterTypeCode,
      "claim.crimeMatterTypeCode"),

  // Case fields
  STAGE_REACHED(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getStageReached,
      ClaimPatch.Builder::stageReachedCode,
      FieldOptions.CRIME_STAGE_REACHED,
      "claimCase.stageReachedCode"),
  UNIQUE_FILE_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getUniqueFileNumber,
      ClaimPatch.Builder::uniqueFileNumber,
      Amendability.UNTIL_ASSESSED,
      "claim.uniqueFileNumber"),
  REPRESENTATION_ORDER_DATE(
      FieldType.DATE,
      String.class,
      CrimeClaimDetails::getRepresentationOrderDate,
      ClaimPatch.Builder::representationOrderDate,
      Amendability.UNTIL_ASSESSED,
      "claim.representationOrderDate"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      CrimeClaimDetails::getCaseEndDate,
      ClaimPatch.Builder::caseConcludedDate,
      Amendability.UNTIL_ASSESSED,
      "claim.caseConcludedDate"),
  STANDARD_FEE_CATEGORY(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getStandardFeeCategory,
      ClaimPatch.Builder::standardFeeCategoryCode,
      FieldOptions.STANDARD_FEE_CATEGORY,
      "claimCase.standardFeeCategoryCode"),
  OUTCOME_FOR_CLIENT(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getOutcome,
      ClaimPatch.Builder::outcomeCode,
      FieldOptions.CRIME_LOWER_OUTCOME,
      "claimCase.outcomeCode"),
  SUSPECTS_DEFENDANTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getSuspectsDefendantsCount,
      ClaimPatch.Builder::suspectsDefendantsCount,
      "claim.suspectsDefendantsCount"),
  POLICE_STATION_COURT_ATTENDANCES_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getPoliceStationCourtAttendancesCount,
      ClaimPatch.Builder::policeStationCourtAttendancesCount,
      "claim.policeStationCourtAttendancesCount"),
  POLICE_STATION_COURT_PRISON_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPoliceStationCourtPrisonId,
      ClaimPatch.Builder::policeStationCourtPrisonId,
      Amendability.UNTIL_ASSESSED,
      "claim.policeStationCourtPrisonId"),
  SCHEME_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getSchemeId,
      ClaimPatch.Builder::schemeId,
      Amendability.UNTIL_ASSESSED,
      "claim.schemeId"),
  DSCC_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getDsccNumber,
      ClaimPatch.Builder::dsccNumber,
      "claim.dsccNumber"),
  MAAT_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getMaatId,
      ClaimPatch.Builder::maatId,
      "claim.maatId"),
  PRISON_LAW_PRIOR_APPROVAL_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPrisonLawPriorApprovalNumber,
      ClaimPatch.Builder::prisonLawPriorApprovalNumber,
      "claim.prisonLawPriorApprovalNumber"),
  IS_DUTY_SOLICITOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CrimeClaimDetails::getIsDutySolicitor,
      ClaimPatch.Builder::isDutySolicitor,
      "claim.dutySolicitor"),
  IS_YOUTH_COURT(
      FieldType.BOOLEAN,
      Boolean.class,
      CrimeClaimDetails::getIsYouthCourt,
      ClaimPatch.Builder::isYouthCourt,
      "claim.youthCourt"),

  // Cost fields
  TRAVEL_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getTravelCosts,
      ClaimPatch.Builder::travelWaitingCostsAmount,
      "claimSummaryFee.travelWaitingCostsAmount"),
  WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getWaitingCosts,
      ClaimPatch.Builder::netWaitingCostsAmount,
      "claimSummaryFee.netWaitingCostsAmount");

  private final CrimeClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, List.of(), Amendability.ALWAYS, claimsApiFieldName);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, List.of(), amendability, claimsApiFieldName);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, options, Amendability.ALWAYS, claimsApiFieldName);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      Amendability amendability,
      String claimsApiFieldName) {
    this.getter = new CrimeClaimViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record CrimeClaimViewFieldGetter<T>(Function<CrimeClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CrimeClaimDetails, T> {}
}
