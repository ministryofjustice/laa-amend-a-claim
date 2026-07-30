package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getClientDateOfBirth,
      ClaimPatch.Builder::clientDateOfBirth),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getClientPostcode,
      ClaimPatch.Builder::clientPostcode),
  IS_ELIGIBLE_CLIENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsEligibleClient,
      ClaimPatch.Builder::isEligibleClient),
  CLIENT_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getClientType,
      ClaimPatch.Builder::clientTypeCode,
      FieldOptions.CLIENT_TYPE),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueClientNumber,
      ClaimPatch.Builder::uniqueClientNumber),
  HOME_OFFICE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getHomeOfficeClientNumber,
      ClaimPatch.Builder::homeOfficeClientNumber),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsPostalApplication,
      ClaimPatch.Builder::isPostalApplicationAccepted),

  // Case type fields
  FEE_CODE(
      FieldType.TEXT, String.class, CivilClaimDetails::getFeeCode, ClaimPatch.Builder::feeCode),
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType1,
      ClaimPatch.Builder::matterTypeCode),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType2,
      ClaimPatch.Builder::matterTypeCode),

  // Case details fields
  SCHEDULE_REFERENCE_CIVIL(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getScheduleReference,
      ClaimPatch.Builder::scheduleReference),
  CASE_ID(FieldType.TEXT, String.class, CivilClaimDetails::getCaseId, ClaimPatch.Builder::caseId),
  CASE_CONCLUDED_CLAIMED_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getCaseConcludedDate,
      ClaimPatch.Builder::caseConcludedDate),
  CASE_STAGE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getCaseStage,
      ClaimPatch.Builder::caseStageCode,
      FieldOptions.CASE_STAGE),
  VALUE_OF_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getValueOfCosts,
      ClaimPatch.Builder::costsDamagesRecoveredAmount),
  PROCUREMENT_AREA(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getProcurementArea,
      ClaimPatch.Builder::procurementAreaCode),
  ACCESS_POINT(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getAccessPoint,
      ClaimPatch.Builder::accessPointCode),
  OUTCOME_FOR_CLIENT(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getOutcome,
      ClaimPatch.Builder::outcomeCode,
      FieldOptions.OUTCOME),
  EXCEPTIONAL_CASE_FUNDING(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getExceptionalCaseFundingReference,
      ClaimPatch.Builder::exceptionalCaseFundingReference),
  CIVIL_LEGAL_ADVICE_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceReference,
      ClaimPatch.Builder::claReferenceNumber),
  CIVIL_LEGAL_ADVICE_EXEMPTION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceExemption,
      ClaimPatch.Builder::claExemptionCode),
  DELIVERY_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getDeliveryLocation,
      ClaimPatch.Builder::deliveryLocation),
  COURT_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCourtLocation,
      ClaimPatch.Builder::courtLocationCode),
  AIT_HEARING_CENTRE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAitHearingCentre,
      ClaimPatch.Builder::aitHearingCentreCode,
      FieldOptions.AIT_HEARING_CENTRE),
  LOCAL_AUTHORITY_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getLocalAuthorityNumber,
      ClaimPatch.Builder::localAuthorityNumber),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getDesignatedAccreditedRepresentative,
      ClaimPatch.Builder::designatedAccreditedRepresentativeCode,
      FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE),
  ADVICE_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdviceTime,
      ClaimPatch.Builder::adviceTime),
  TRAVEL_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getTravelTime,
      ClaimPatch.Builder::travelTime),
  WAITING_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getWaitingTime,
      ClaimPatch.Builder::waitingTime),
  ADDITIONAL_TRAVEL_PAYMENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsAdditionalTravelPayment,
      ClaimPatch.Builder::isAdditionalTravelPayment),
  FOLLOW_ON_WORK(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getFollowOnWork,
      ClaimPatch.Builder::followOnWork),
  TOLERANCE_INDICATOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsToleranceApplicable,
      ClaimPatch.Builder::isToleranceApplicable),
  LEGACY_CASE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLegacyCase,
      ClaimPatch.Builder::isLegacyCase),
  MEETINGS_ATTENDED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getMeetingsAttended,
      ClaimPatch.Builder::meetingsAttendedCode,
      FieldOptions.MEETINGS_ATTENDED),
  ADVICE_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAdviceType,
      ClaimPatch.Builder::adviceTypeCode,
      FieldOptions.ADVICE_TYPE),
  TRANSFER_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getTransferDate,
      ClaimPatch.Builder::transferDate),
  MEDICAL_REPORTS_CLAIMED(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getMedicalReportsClaimed,
      ClaimPatch.Builder::medicalReportsCount),
  EXEMPTION_CRITERIA_SATISFIED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getExemptionCriteriaSatisfied,
      ClaimPatch.Builder::exemptionCriteriaSatisfied,
      FieldOptions.EXEMPTION_CRITERIA_SATISFIED),
  IRC_SURGERY(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsIrcSurgery,
      ClaimPatch.Builder::isIrcSurgery),
  SURGERY_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getSurgeryDate,
      ClaimPatch.Builder::surgeryDate),
  SURGERY_CLIENTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryClientsCount,
      ClaimPatch.Builder::surgeryClientsCount),
  SURGERY_MATTERS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryMattersCount,
      ClaimPatch.Builder::surgeryMattersCount),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMentalHealthTribunalReference,
      ClaimPatch.Builder::mentalHealthTribunalReference),
  IS_NRM_ADVICE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsNrmAdvice,
      ClaimPatch.Builder::isNrmAdvice),

  // Cost fields
  COUNSELS_COST(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getCounselsCost,
      ClaimPatch.Builder::netCounselCostsAmount),
  TRAVEL_AND_WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getTravelAndWaitingCosts,
      ClaimPatch.Builder::travelWaitingCostsAmount),
  DETENTION_TRAVEL(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getDetentionTravelWaitingCosts,
      ClaimPatch.Builder::detentionTravelWaitingCostsAmount),
  JR_FORM_FILLING(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getJrFormFillingCost,
      ClaimPatch.Builder::jrFormFillingAmount),
  ADJOURNED_HEARING_FEE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdjournedHearing,
      ClaimPatch.Builder::adjournedHearingFeeAmount),
  CMRH_TELEPHONE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getCmrhTelephone,
      ClaimPatch.Builder::cmrhTelephoneCount),
  CMRH_ORAL(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getCmrhOral,
      ClaimPatch.Builder::cmrhOralCount),
  HOME_OFFICE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getHoInterview,
      ClaimPatch.Builder::hoInterview),
  SUBSTANTIVE_HEARING(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getSubstantiveHearing,
      ClaimPatch.Builder::isSubstantiveHearing),
  IS_LONDON_RATE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLondonRate,
      ClaimPatch.Builder::isLondonRate),
  PRIOR_AUTHORITY_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getPriorAuthorityReference,
      ClaimPatch.Builder::priorAuthorityReference);

  private final CivilClaimViewFieldGetter<?> getter;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher) {
    this(fieldType, patchType, getter, patcher, List.of());
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      List<FieldOption> options) {
    this.getter = new CivilClaimViewFieldGetter<>(getter);
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
  }

  public record CivilClaimViewFieldGetter<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CivilClaimDetails, T> {}
}
