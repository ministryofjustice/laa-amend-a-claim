package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.Amendability;
import uk.gov.justice.laa.amend.claim.models.enums.FieldType;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getClientDateOfBirth,
      ClaimPatch.Builder::clientDateOfBirth,
      "client.clientDateOfBirth"),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getClientPostcode,
      ClaimPatch.Builder::clientPostcode,
      "client.clientPostcode"),
  IS_ELIGIBLE_CLIENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsEligibleClient,
      ClaimPatch.Builder::isEligibleClient,
      "claimSummaryFee.isEligibleClient"),
  CLIENT_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getClientType,
      ClaimPatch.Builder::clientTypeCode,
      FieldOptions.CLIENT_TYPE,
      "client.clientTypeCode"),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueClientNumber,
      ClaimPatch.Builder::uniqueClientNumber,
      "client.uniqueClientNumber"),
  HOME_OFFICE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getHomeOfficeClientNumber,
      ClaimPatch.Builder::homeOfficeClientNumber,
      "client.homeOfficeClientNumber"),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsPostalApplication,
      ClaimPatch.Builder::isPostalApplicationAccepted,
      "claimCase.isPostalApplicationAccepted"),

  // Case type fields
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType1,
      ClaimPatch.Builder::matterTypeCode,
      "claim.matterTypeCode"),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType2,
      ClaimPatch.Builder::matterTypeCode,
      "claim.matterTypeCode"),

  // Case details fields
  STAGE_REACHED(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getStageReached,
      ClaimPatch.Builder::stageReachedCode,
      "claimCase.stageReachedCode"),
  SCHEDULE_REFERENCE_CIVIL(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getScheduleReference,
      ClaimPatch.Builder::scheduleReference,
      "claim.scheduleReference"),
  CASE_ID(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCaseId,
      ClaimPatch.Builder::caseId,
      "claimCase.caseId"),
  CASE_CONCLUDED_CLAIMED_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getCaseConcludedDate,
      ClaimPatch.Builder::caseConcludedDate,
      Amendability.UNTIL_ASSESSED,
      "claim.caseConcludedDate"),
  UNIQUE_FILE_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueFileNumber,
      ClaimPatch.Builder::uniqueFileNumber,
      "claim.uniqueFileNumber"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getCaseConcludedDate,
      ClaimPatch.Builder::caseConcludedDate,
      "claim.caseConcludedDate"),
  CASE_STAGE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getCaseStage,
      ClaimPatch.Builder::caseStageCode,
      FieldOptions.CASE_STAGE,
      "claimCase.caseStageCode"),
  VALUE_OF_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getValueOfCosts,
      ClaimPatch.Builder::costsDamagesRecoveredAmount,
      "claimSummaryFee.costsDamagesRecoveredAmount"),
  PROCUREMENT_AREA(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getProcurementArea,
      ClaimPatch.Builder::procurementAreaCode,
      "claim.procurementAreaCode"),
  ACCESS_POINT(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getAccessPoint,
      ClaimPatch.Builder::accessPointCode,
      "claim.accessPointCode"),
  OUTCOME_FOR_CLIENT(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getOutcome,
      ClaimPatch.Builder::outcomeCode,
      "claimCase.outcomeCode"),
  EXCEPTIONAL_CASE_FUNDING(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getExceptionalCaseFundingReference,
      ClaimPatch.Builder::exceptionalCaseFundingReference,
      "claimCase.exceptionalCaseFundingReference"),
  CIVIL_LEGAL_ADVICE_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceReference,
      ClaimPatch.Builder::claReferenceNumber,
      "client.claReferenceNumber"),
  CIVIL_LEGAL_ADVICE_EXEMPTION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceExemption,
      ClaimPatch.Builder::claExemptionCode,
      "client.claExemptionCode"),
  DELIVERY_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getDeliveryLocation,
      ClaimPatch.Builder::deliveryLocation,
      "claim.deliveryLocation"),
  COURT_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCourtLocation,
      ClaimPatch.Builder::courtLocationCode,
      "claimSummaryFee.courtLocationCode"),
  AIT_HEARING_CENTRE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAitHearingCentre,
      ClaimPatch.Builder::aitHearingCentreCode,
      FieldOptions.AIT_HEARING_CENTRE,
      "claimSummaryFee.aitHearingCentreCode"),
  LOCAL_AUTHORITY_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getLocalAuthorityNumber,
      ClaimPatch.Builder::localAuthorityNumber,
      "claimSummaryFee.localAuthorityNumber"),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getDesignatedAccreditedRepresentative,
      ClaimPatch.Builder::designatedAccreditedRepresentativeCode,
      FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE,
      "claimCase.designatedAccreditedRepresentativeCode"),
  ADVICE_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdviceTime,
      ClaimPatch.Builder::adviceTime,
      "claimSummaryFee.adviceTime"),
  TRAVEL_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getTravelTime,
      ClaimPatch.Builder::travelTime,
      "claimSummaryFee.travelTime"),
  WAITING_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getWaitingTime,
      ClaimPatch.Builder::waitingTime,
      "claimSummaryFee.waitingTime"),
  ADDITIONAL_TRAVEL_PAYMENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsAdditionalTravelPayment,
      ClaimPatch.Builder::isAdditionalTravelPayment,
      "claimSummaryFee.isAdditionalTravelPayment"),
  FOLLOW_ON_WORK(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getFollowOnWork,
      ClaimPatch.Builder::followOnWork,
      "claimCase.followOnWork"),
  TOLERANCE_INDICATOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsToleranceApplicable,
      ClaimPatch.Builder::isToleranceApplicable,
      "claimSummaryFee.isToleranceApplicable"),
  LEGACY_CASE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLegacyCase,
      ClaimPatch.Builder::isLegacyCase,
      "claimCase.isLegacyCase"),
  MEETINGS_ATTENDED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getMeetingsAttended,
      ClaimPatch.Builder::meetingsAttendedCode,
      FieldOptions.MEETINGS_ATTENDED,
      "claimSummaryFee.meetingsAttendedCode"),
  ADVICE_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAdviceType,
      ClaimPatch.Builder::adviceTypeCode,
      FieldOptions.ADVICE_TYPE,
      "claimSummaryFee.adviceTypeCode"),
  TRANSFER_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getTransferDate,
      ClaimPatch.Builder::transferDate,
      "claimCase.transferDate"),
  MEDICAL_REPORTS_CLAIMED(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getMedicalReportsClaimed,
      ClaimPatch.Builder::medicalReportsCount,
      "claimSummaryFee.medicalReportsCount"),
  EXEMPTION_CRITERIA_SATISFIED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getExemptionCriteriaSatisfied,
      ClaimPatch.Builder::exemptionCriteriaSatisfied,
      FieldOptions.EXEMPTION_CRITERIA_SATISFIED,
      "claimCase.exemptionCriteriaSatisfied"),
  IRC_SURGERY(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsIrcSurgery,
      ClaimPatch.Builder::isIrcSurgery,
      "claimSummaryFee.isIrcSurgery"),
  SURGERY_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getSurgeryDate,
      ClaimPatch.Builder::surgeryDate,
      "claimSummaryFee.surgeryDate"),
  SURGERY_CLIENTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryClientsCount,
      ClaimPatch.Builder::surgeryClientsCount,
      "claimSummaryFee.surgeryClientsCount"),
  SURGERY_MATTERS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryMattersCount,
      ClaimPatch.Builder::surgeryMattersCount,
      "claimSummaryFee.surgeryMattersCount"),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMentalHealthTribunalReference,
      ClaimPatch.Builder::mentalHealthTribunalReference,
      "claimCase.mentalHealthTribunalReference"),
  IS_NRM_ADVICE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsNrmAdvice,
      ClaimPatch.Builder::isNrmAdvice,
      "claimCase.isNrmAdvice"),

  // Cost fields
  COUNSELS_COST(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getCounselsCost,
      ClaimPatch.Builder::netCounselCostsAmount,
      "claimSummaryFee.netCounselCostsAmount",
      "fee.netCostOfCounselAmount"),
  TRAVEL_AND_WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getTravelAndWaitingCosts,
      ClaimPatch.Builder::travelWaitingCostsAmount,
      "claimSummaryFee.travelWaitingCostsAmount",
      "fee.travelAndWaitingCostsAmount"),
  DETENTION_TRAVEL(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getDetentionTravelWaitingCosts,
      ClaimPatch.Builder::detentionTravelWaitingCostsAmount,
      "claimSummaryFee.detentionTravelWaitingCostsAmount",
      "fee.detentionTravelAndWaitingCostsAmount"),
  JR_FORM_FILLING(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getJrFormFillingCost,
      ClaimPatch.Builder::jrFormFillingAmount,
      "claimSummaryFee.jrFormFillingAmount",
      "fee.jrFormFillingAmount"),
  ADJOURNED_HEARING_FEE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdjournedHearing,
      ClaimPatch.Builder::adjournedHearingFeeAmount,
      "claimSummaryFee.adjournedHearingFeeAmount",
      "fee.boltOnAdjournedHearingFee"),
  CMRH_TELEPHONE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getCmrhTelephone,
      ClaimPatch.Builder::cmrhTelephoneCount,
      "claimSummaryFee.cmrhTelephoneCount",
      "fee.boltOnCmrhTelephoneFee"),
  CMRH_ORAL(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getCmrhOral,
      ClaimPatch.Builder::cmrhOralCount,
      "claimSummaryFee.cmrhOralCount",
      "fee.boltOnCmrhOralFee"),
  HOME_OFFICE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getHoInterview,
      ClaimPatch.Builder::hoInterview,
      "claimSummaryFee.hoInterview",
      "fee.boltOnHomeOfficeInterviewFee"),
  SUBSTANTIVE_HEARING(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getSubstantiveHearing,
      ClaimPatch.Builder::isSubstantiveHearing,
      "claimSummaryFee.isSubstantiveHearing",
      "fee.boltOnSubstantiveHearingFee"),
  IS_LONDON_RATE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLondonRate,
      ClaimPatch.Builder::isLondonRate,
      "claimSummaryFee.isLondonRate"),
  PRIOR_AUTHORITY_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getPriorAuthorityReference,
      ClaimPatch.Builder::priorAuthorityReference,
      "claimSummaryFee.priorAuthorityReference");

  private final CivilClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final String feeApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        null);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
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
        null);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this(
        fieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        feeApiFieldName);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, List.of(), amendability, claimsApiFieldName, null);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options,
      Amendability amendability,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this.getter = new CivilClaimViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.feeApiFieldName = feeApiFieldName;
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record CivilClaimViewFieldGetter<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CivilClaimDetails, T> {}
}
