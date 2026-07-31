package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(new Accessor<>(CivilClaimDetails::getClientDateOfBirth), FieldType.DATE),
  POSTCODE(new Accessor<>(CivilClaimDetails::getClientPostcode)),
  IS_ELIGIBLE_CLIENT(new Accessor<>(CivilClaimDetails::getIsEligibleClient), FieldType.BOOLEAN),
  CLIENT_TYPE(new Accessor<>(CivilClaimDetails::getClientType), FieldOptions.CLIENT_TYPE),
  UNIQUE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getUniqueClientNumber)),
  HOME_OFFICE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getHomeOfficeClientNumber)),
  IS_POSTAL_APPLICATION_ACCEPTED(
      new Accessor<>(CivilClaimDetails::getIsPostalApplication), FieldType.BOOLEAN),

  // Case type fields
  FEE_CODE(new Accessor<>(CivilClaimDetails::getFeeCode)),
  MATTER_TYPE_CODE_1(new Accessor<>(CivilClaimDetails::getMatterType1)),
  MATTER_TYPE_CODE_2(new Accessor<>(CivilClaimDetails::getMatterType2)),

  // Case details fields
  SCHEDULE_REFERENCE_CIVIL(new Accessor<>(CivilClaimDetails::getScheduleReference)),
  CASE_ID(new Accessor<>(CivilClaimDetails::getCaseId)),
  CASE_CONCLUDED_CLAIMED_DATE(
      new Accessor<>(CivilClaimDetails::getCaseConcludedDate), FieldType.DATE),
  CASE_STAGE(new Accessor<>(CivilClaimDetails::getCaseStage), FieldOptions.CASE_STAGE),
  VALUE_OF_COSTS(new Accessor<>(CivilClaimDetails::getValueOfCosts), FieldType.BIG_DECIMAL),
  PROCUREMENT_AREA(new Accessor<>(CivilClaimDetails::getProcurementArea)),
  ACCESS_POINT(new Accessor<>(CivilClaimDetails::getAccessPoint)),
  OUTCOME_FOR_CLIENT(new Accessor<>(CivilClaimDetails::getOutcome), FieldOptions.OUTCOME),
  EXCEPTIONAL_CASE_FUNDING(new Accessor<>(CivilClaimDetails::getExceptionalCaseFundingReference)),
  CIVIL_LEGAL_ADVICE_REFERENCE(new Accessor<>(CivilClaimDetails::getCivilLegalAdviceReference)),
  CIVIL_LEGAL_ADVICE_EXEMPTION(new Accessor<>(CivilClaimDetails::getCivilLegalAdviceExemption)),
  DELIVERY_LOCATION(new Accessor<>(CivilClaimDetails::getDeliveryLocation)),
  COURT_LOCATION(new Accessor<>(CivilClaimDetails::getCourtLocation)),
  AIT_HEARING_CENTRE(
      new Accessor<>(CivilClaimDetails::getAitHearingCentre), FieldOptions.AIT_HEARING_CENTRE),
  LOCAL_AUTHORITY_NUMBER(new Accessor<>(CivilClaimDetails::getLocalAuthorityNumber)),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      new Accessor<>(CivilClaimDetails::getDesignatedAccreditedRepresentative),
      FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE),
  ADVICE_TIME(new Accessor<>(CivilClaimDetails::getAdviceTime), FieldType.NUMBER),
  TRAVEL_TIME(new Accessor<>(CivilClaimDetails::getTravelTime), FieldType.NUMBER),
  WAITING_TIME(new Accessor<>(CivilClaimDetails::getWaitingTime), FieldType.NUMBER),
  ADDITIONAL_TRAVEL_PAYMENT(
      new Accessor<>(CivilClaimDetails::getIsAdditionalTravelPayment), FieldType.BOOLEAN),
  FOLLOW_ON_WORK(new Accessor<>(CivilClaimDetails::getFollowOnWork)),
  TOLERANCE_INDICATOR(
      new Accessor<>(CivilClaimDetails::getIsToleranceApplicable), FieldType.BOOLEAN),
  LEGACY_CASE(new Accessor<>(CivilClaimDetails::getIsLegacyCase), FieldType.BOOLEAN),
  MEETINGS_ATTENDED(
      new Accessor<>(CivilClaimDetails::getMeetingsAttended), FieldOptions.MEETINGS_ATTENDED),
  ADVICE_TYPE(new Accessor<>(CivilClaimDetails::getAdviceType), FieldOptions.ADVICE_TYPE),
  TRANSFER_DATE(new Accessor<>(CivilClaimDetails::getTransferDate), FieldType.DATE),
  MEDICAL_REPORTS_CLAIMED(
      new Accessor<>(CivilClaimDetails::getMedicalReportsClaimed), FieldType.NUMBER),
  EXEMPTION_CRITERIA_SATISFIED(
      new Accessor<>(CivilClaimDetails::getExemptionCriteriaSatisfied),
      FieldOptions.EXEMPTION_CRITERIA_SATISFIED),
  IRC_SURGERY(new Accessor<>(CivilClaimDetails::getIsIrcSurgery), FieldType.BOOLEAN),
  SURGERY_DATE(new Accessor<>(CivilClaimDetails::getSurgeryDate), FieldType.DATE),
  SURGERY_CLIENTS_COUNT(
      new Accessor<>(CivilClaimDetails::getSurgeryClientsCount), FieldType.NUMBER),
  SURGERY_MATTERS_COUNT(
      new Accessor<>(CivilClaimDetails::getSurgeryMattersCount), FieldType.NUMBER),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      new Accessor<>(CivilClaimDetails::getMentalHealthTribunalReference)),
  IS_NRM_ADVICE(new Accessor<>(CivilClaimDetails::getIsNrmAdvice), FieldType.BOOLEAN),

  // Cost fields
  COUNSELS_COST(new Accessor<>(CivilClaimDetails::getCounselsCost), FieldType.BIG_DECIMAL),
  TRAVEL_AND_WAITING_COSTS(
      new Accessor<>(CivilClaimDetails::getTravelAndWaitingCosts), FieldType.BIG_DECIMAL),
  DETENTION_TRAVEL(
      new Accessor<>(CivilClaimDetails::getDetentionTravelWaitingCosts), FieldType.BIG_DECIMAL),
  JR_FORM_FILLING(new Accessor<>(CivilClaimDetails::getJrFormFillingCost), FieldType.BIG_DECIMAL),
  ADJOURNED_HEARING_FEE(new Accessor<>(CivilClaimDetails::getAdjournedHearing), FieldType.NUMBER),
  CMRH_TELEPHONE(new Accessor<>(CivilClaimDetails::getCmrhTelephone), FieldType.NUMBER),
  CMRH_ORAL(new Accessor<>(CivilClaimDetails::getCmrhOral), FieldType.NUMBER),
  HOME_OFFICE(new Accessor<>(CivilClaimDetails::getHoInterview), FieldType.NUMBER),
  SUBSTANTIVE_HEARING(new Accessor<>(CivilClaimDetails::getSubstantiveHearing), FieldType.BOOLEAN),
  IS_LONDON_RATE(new Accessor<>(CivilClaimDetails::getIsLondonRate), FieldType.BOOLEAN),
  PRIOR_AUTHORITY_REFERENCE(new Accessor<>(CivilClaimDetails::getPriorAuthorityReference)),
  ;

  private final Accessor<?> accessor;
  private final FieldType type;
  private final List<FieldOption> options;

  CivilClaimDetailsViewField(Accessor<?> accessor) {
    this(accessor, FieldType.TEXT);
  }

  CivilClaimDetailsViewField(Accessor<?> accessor, FieldType type) {
    this(accessor, type, List.of());
  }

  CivilClaimDetailsViewField(Accessor<?> accessor, List<FieldOption> options) {
    this(accessor, FieldType.ENUM, options);
  }

  CivilClaimDetailsViewField(Accessor<?> accessor, FieldType type, List<FieldOption> options) {
    this.accessor = accessor;
    this.type = type;
    this.options = List.copyOf(options);
  }

  public record Accessor<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<CivilClaimDetails, T> {}
}
