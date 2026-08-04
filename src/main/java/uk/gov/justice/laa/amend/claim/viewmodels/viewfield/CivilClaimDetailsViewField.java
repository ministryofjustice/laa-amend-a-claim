package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getClientDateOfBirth,
      Builder::clientDateOfBirth),
  POSTCODE(
      FieldType.TEXT, String.class, CivilClaimDetails::getClientPostcode, Builder::clientPostcode),
  IS_ELIGIBLE_CLIENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsEligibleClient,
      Builder::isEligibleClient),
  CLIENT_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getClientType,
      Builder::clientTypeCode,
      FieldOptions.CLIENT_TYPE),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueClientNumber,
      Builder::uniqueClientNumber),
  HOME_OFFICE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getHomeOfficeClientNumber,
      Builder::homeOfficeClientNumber),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsPostalApplication,
      Builder::isPostalApplicationAccepted),

  // Case type fields
  FEE_CODE(FieldType.TEXT, String.class, CivilClaimDetails::getFeeCode, Builder::feeCode),
  MATTER_TYPE_CODE_1(
      FieldType.TEXT, String.class, CivilClaimDetails::getMatterType1, Builder::matterTypeCode),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT, String.class, CivilClaimDetails::getMatterType2, Builder::matterTypeCode),

  // Case details fields
  STAGE_REACHED(
      FieldType.TEXT, String.class, ClaimDetails::getStageReached, Builder::stageReachedCode),
  SCHEDULE_REFERENCE_CIVIL(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getScheduleReference,
      Builder::scheduleReference),
  CASE_ID(FieldType.TEXT, String.class, CivilClaimDetails::getCaseId, Builder::caseId),
  CASE_CONCLUDED_CLAIMED_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getCaseConcludedDate,
      Builder::caseConcludedDate),
  CASE_STAGE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getCaseStage,
      Builder::caseStageCode,
      FieldOptions.CASE_STAGE),
  VALUE_OF_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getValueOfCosts,
      Builder::costsDamagesRecoveredAmount),
  PROCUREMENT_AREA(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getProcurementArea,
      Builder::procurementAreaCode),
  ACCESS_POINT(
      FieldType.TEXT, String.class, CivilClaimDetails::getAccessPoint, Builder::accessPointCode),
  OUTCOME_FOR_CLIENT(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.OUTCOME),
  EXCEPTIONAL_CASE_FUNDING(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getExceptionalCaseFundingReference,
      Builder::exceptionalCaseFundingReference),
  CIVIL_LEGAL_ADVICE_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceReference,
      Builder::claReferenceNumber),
  CIVIL_LEGAL_ADVICE_EXEMPTION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceExemption,
      Builder::claExemptionCode),
  DELIVERY_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getDeliveryLocation,
      Builder::deliveryLocation),
  COURT_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCourtLocation,
      Builder::courtLocationCode),
  AIT_HEARING_CENTRE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAitHearingCentre,
      Builder::aitHearingCentreCode,
      FieldOptions.AIT_HEARING_CENTRE),
  LOCAL_AUTHORITY_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getLocalAuthorityNumber,
      Builder::localAuthorityNumber),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getDesignatedAccreditedRepresentative,
      Builder::designatedAccreditedRepresentativeCode,
      FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE),
  ADVICE_TIME(
      FieldType.NUMBER, Integer.class, CivilClaimDetails::getAdviceTime, Builder::adviceTime),
  TRAVEL_TIME(
      FieldType.NUMBER, Integer.class, CivilClaimDetails::getTravelTime, Builder::travelTime),
  WAITING_TIME(
      FieldType.NUMBER, Integer.class, CivilClaimDetails::getWaitingTime, Builder::waitingTime),
  ADDITIONAL_TRAVEL_PAYMENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsAdditionalTravelPayment,
      Builder::isAdditionalTravelPayment),
  FOLLOW_ON_WORK(
      FieldType.TEXT, String.class, CivilClaimDetails::getFollowOnWork, Builder::followOnWork),
  TOLERANCE_INDICATOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsToleranceApplicable,
      Builder::isToleranceApplicable),
  LEGACY_CASE(
      FieldType.BOOLEAN, Boolean.class, CivilClaimDetails::getIsLegacyCase, Builder::isLegacyCase),
  MEETINGS_ATTENDED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getMeetingsAttended,
      Builder::meetingsAttendedCode,
      FieldOptions.MEETINGS_ATTENDED),
  ADVICE_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAdviceType,
      Builder::adviceTypeCode,
      FieldOptions.ADVICE_TYPE),
  TRANSFER_DATE(
      FieldType.DATE, String.class, CivilClaimDetails::getTransferDate, Builder::transferDate),
  MEDICAL_REPORTS_CLAIMED(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getMedicalReportsClaimed,
      Builder::medicalReportsCount),
  EXEMPTION_CRITERIA_SATISFIED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getExemptionCriteriaSatisfied,
      Builder::exemptionCriteriaSatisfied,
      FieldOptions.EXEMPTION_CRITERIA_SATISFIED),
  IRC_SURGERY(
      FieldType.BOOLEAN, Boolean.class, CivilClaimDetails::getIsIrcSurgery, Builder::isIrcSurgery),
  SURGERY_DATE(
      FieldType.DATE, String.class, CivilClaimDetails::getSurgeryDate, Builder::surgeryDate),
  SURGERY_CLIENTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryClientsCount,
      Builder::surgeryClientsCount),
  SURGERY_MATTERS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryMattersCount,
      Builder::surgeryMattersCount),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMentalHealthTribunalReference,
      Builder::mentalHealthTribunalReference),
  IS_NRM_ADVICE(
      FieldType.BOOLEAN, Boolean.class, CivilClaimDetails::getIsNrmAdvice, Builder::isNrmAdvice),

  // Cost fields
  COUNSELS_COST(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getCounselsCost,
      Builder::netCounselCostsAmount),
  TRAVEL_AND_WAITING_COSTS(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getTravelAndWaitingCosts,
      Builder::travelWaitingCostsAmount),
  DETENTION_TRAVEL(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getDetentionTravelWaitingCosts,
      Builder::detentionTravelWaitingCostsAmount),
  JR_FORM_FILLING(
      FieldType.BIG_DECIMAL,
      BigDecimal.class,
      CivilClaimDetails::getJrFormFillingCost,
      Builder::jrFormFillingAmount),
  ADJOURNED_HEARING_FEE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdjournedHearing,
      Builder::adjournedHearingFeeAmount),
  CMRH_TELEPHONE(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getCmrhTelephone,
      Builder::cmrhTelephoneCount),
  CMRH_ORAL(
      FieldType.NUMBER, Integer.class, CivilClaimDetails::getCmrhOral, Builder::cmrhOralCount),
  HOME_OFFICE(
      FieldType.NUMBER, Integer.class, CivilClaimDetails::getHoInterview, Builder::hoInterview),
  SUBSTANTIVE_HEARING(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getSubstantiveHearing,
      Builder::isSubstantiveHearing),
  IS_LONDON_RATE(
      FieldType.BOOLEAN, Boolean.class, CivilClaimDetails::getIsLondonRate, Builder::isLondonRate),
  PRIOR_AUTHORITY_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getPriorAuthorityReference,
      Builder::priorAuthorityReference);

  private final CivilClaimViewFieldGetter<?> getter;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher) {
    this(fieldType, patchType, getter, patcher, List.of());
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options) {
    this.getter = new CivilClaimViewFieldGetter<>(getter);
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
  }

  public record CivilClaimViewFieldGetter<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CivilClaimDetails, T> {}
}
