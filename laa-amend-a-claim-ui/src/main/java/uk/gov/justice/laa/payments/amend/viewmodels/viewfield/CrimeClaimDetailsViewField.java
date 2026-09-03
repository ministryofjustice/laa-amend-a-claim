package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.Amendability;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;

@Getter
public enum CrimeClaimDetailsViewField implements ClaimViewField<CrimeClaimDetails> {
  // Client fields
  INITIAL(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getClientForename,
      Builder::clientForename,
      "client.clientForename"),

  // Case type fields
  MATTER_TYPE_CODE(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getMatterTypeCode,
      Builder::crimeMatterTypeCode,
      "claim.crimeMatterTypeCode"),

  // Case fields
  STAGE_REACHED(
      FieldType.ENUM,
      String.class,
      ClaimDetails::getStageReached,
      Builder::stageReachedCode,
      FieldOptions.CRIME_STAGE_REACHED,
      "claimCase.stageReachedCode"),
  UNIQUE_FILE_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getUniqueFileNumber,
      Builder::uniqueFileNumber,
      Amendability.UNTIL_ASSESSED,
      "claim.uniqueFileNumber"),
  REPRESENTATION_ORDER_DATE(
      FieldType.DATE,
      String.class,
      CrimeClaimDetails::getRepresentationOrderDate,
      Builder::representationOrderDate,
      Amendability.UNTIL_ASSESSED,
      "claim.representationOrderDate"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      CrimeClaimDetails::getCaseEndDate,
      Builder::caseConcludedDate,
      Amendability.UNTIL_ASSESSED,
      "claim.caseConcludedDate"),
  STANDARD_FEE_CATEGORY(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getStandardFeeCategory,
      Builder::standardFeeCategoryCode,
      FieldOptions.STANDARD_FEE_CATEGORY,
      "claimCase.standardFeeCategoryCode"),
  OUTCOME_FOR_CLIENT(
      FieldType.ENUM,
      String.class,
      CrimeClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.CRIME_LOWER_OUTCOME,
      "claimCase.outcomeCode"),
  SUSPECTS_DEFENDANTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getSuspectsDefendantsCount,
      Builder::suspectsDefendantsCount,
      "claim.suspectsDefendantsCount"),
  POLICE_STATION_COURT_ATTENDANCES_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CrimeClaimDetails::getPoliceStationCourtAttendancesCount,
      Builder::policeStationCourtAttendancesCount,
      "claim.policeStationCourtAttendancesCount"),
  POLICE_STATION_COURT_PRISON_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPoliceStationCourtPrisonId,
      Builder::policeStationCourtPrisonId,
      Amendability.UNTIL_ASSESSED,
      "claim.policeStationCourtPrisonId"),
  SCHEME_ID(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getSchemeId,
      Builder::schemeId,
      NO_OPTIONS,
      Amendability.UNTIL_ASSESSED,
      "claim.schemeId",
      "fee.schemeId"),
  DSCC_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getDsccNumber,
      Builder::dsccNumber,
      "claim.dsccNumber"),
  MAAT_ID(
      FieldType.TEXT, String.class, CrimeClaimDetails::getMaatId, Builder::maatId, "claim.maatId"),
  PRISON_LAW_PRIOR_APPROVAL_NUMBER(
      FieldType.TEXT,
      String.class,
      CrimeClaimDetails::getPrisonLawPriorApprovalNumber,
      Builder::prisonLawPriorApprovalNumber,
      "claim.prisonLawPriorApprovalNumber"),
  IS_DUTY_SOLICITOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CrimeClaimDetails::getIsDutySolicitor,
      Builder::isDutySolicitor,
      "claim.dutySolicitor"),
  IS_YOUTH_COURT(
      FieldType.BOOLEAN,
      Boolean.class,
      CrimeClaimDetails::getIsYouthCourt,
      Builder::isYouthCourt,
      "claim.youthCourt"),

  // Cost fields
  TRAVEL_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getTravelCosts,
      Builder::travelWaitingCostsAmount,
      "claimSummaryFee.travelWaitingCostsAmount",
      "fee.netTravelCostsAmount"),
  WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CrimeClaimDetails::getWaitingCosts,
      Builder::netWaitingCostsAmount,
      "claimSummaryFee.netWaitingCostsAmount",
      "fee.netWaitingCostsAmount");

  private final CrimeClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final String feeApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        options,
        Amendability.ALWAYS,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        NO_OPTIONS,
        Amendability.ALWAYS,
        claimsApiFieldName,
        feeApiFieldName);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        NO_OPTIONS,
        amendability,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CrimeClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CrimeClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options,
      Amendability amendability,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this.getter = new CrimeClaimViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.feeApiFieldName = feeApiFieldName;
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record CrimeClaimViewFieldGetter<T>(Function<CrimeClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CrimeClaimDetails, T> {}
}
