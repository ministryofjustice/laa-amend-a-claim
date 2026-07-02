package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  DATE_OF_BIRTH(new Accessor<>(CivilClaimDetails::getClientDateOfBirth), FieldType.DATE),
  POSTCODE(new Accessor<>(CivilClaimDetails::getClientPostcode)),
  IS_ELIGIBLE_CLIENT(new Accessor<>(CivilClaimDetails::getIsEligibleClient)),
  CLIENT_TYPE(new Accessor<>(CivilClaimDetails::getClientType)),
  UNIQUE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getUniqueClientNumber)),
  HOME_OFFICE_CLIENT_NUMBER(new Accessor<>(CivilClaimDetails::getHomeOfficeClientNumber)),
  IS_POSTAL_APPLICATION_ACCEPTED(new Accessor<>(CivilClaimDetails::getIsPostalApplication)),

  // Case type fields
  FEE_CODE(new Accessor<>(CivilClaimDetails::getFeeCode)),
  MATTER_TYPE_CODE_1(new Accessor<>(CivilClaimDetails::getMatterType1)),
  MATTER_TYPE_CODE_2(new Accessor<>(CivilClaimDetails::getMatterType2)),

  // Case details fields
  SCHEDULE_REFERENCE_CIVIL(new Accessor<>(CivilClaimDetails::getScheduleReference)),
  CASE_ID(new Accessor<>(CivilClaimDetails::getCaseId)),
  CASE_REFERENCE_NUMBER(new Accessor<>(CivilClaimDetails::getCaseReferenceNumber)),
  CASE_START_DATE(new Accessor<>(CivilClaimDetails::getCaseStartDate), FieldType.DATE),
  CASE_CONCLUDED_CLAIMED_DATE(
      new Accessor<>(CivilClaimDetails::getCaseConcludedDate), FieldType.DATE),
  UNIQUE_FILE_NUMBER(new Accessor<>(CivilClaimDetails::getUniqueFileNumber)),
  CASE_STAGE(new Accessor<>(CivilClaimDetails::getCaseStage)),
  VALUE_OF_COSTS(new Accessor<>(CivilClaimDetails::getValueOfCosts)),
  PROCUREMENT_AREA(new Accessor<>(CivilClaimDetails::getProcurementArea)),
  ACCESS_POINT(new Accessor<>(CivilClaimDetails::getAccessPoint)),
  STAGE_REACHED(new Accessor<>(CivilClaimDetails::getStageReached)),
  OUTCOME_FOR_CLIENT(new Accessor<>(CivilClaimDetails::getOutcome)),
  EXCEPTIONAL_CASE_FUNDING(new Accessor<>(CivilClaimDetails::getExceptionalCaseFundingReference)),
  CIVIL_LEGAL_ADVICE_REFERENCE(new Accessor<>(CivilClaimDetails::getCivilLegalAdviceReference)),
  CIVIL_LEGAL_ADVICE_EXEMPTION(new Accessor<>(CivilClaimDetails::getCivilLegalAdviceExemption)),
  DELIVERY_LOCATION(new Accessor<>(CivilClaimDetails::getDeliveryLocation)),
  COURT_LOCATION(new Accessor<>(CivilClaimDetails::getCourtLocation)),
  AIT_HEARING_CENTRE(new Accessor<>(CivilClaimDetails::getAitHearingCentre)),
  LOCAL_AUTHORITY_NUMBER(new Accessor<>(CivilClaimDetails::getLocalAuthorityNumber)),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      new Accessor<>(CivilClaimDetails::getDesignatedAccreditedRepresentative)),
  ADVICE_TIME(new Accessor<>(CivilClaimDetails::getAdviceTime)),
  TRAVEL_TIME(new Accessor<>(CivilClaimDetails::getTravelTime)),
  WAITING_TIME(new Accessor<>(CivilClaimDetails::getWaitingTime)),
  ADDITIONAL_TRAVEL_PAYMENT(new Accessor<>(CivilClaimDetails::getIsAdditionalTravelPayment)),
  FOLLOW_ON_WORK(new Accessor<>(CivilClaimDetails::getFollowOnWork)),
  TOLERANCE_INDICATOR(new Accessor<>(CivilClaimDetails::getIsToleranceApplicable)),
  LEGACY_CASE(new Accessor<>(CivilClaimDetails::getIsLegacyCase)),
  MEETINGS_ATTENDED(new Accessor<>(CivilClaimDetails::getMeetingsAttended)),
  ADVICE_TYPE(new Accessor<>(CivilClaimDetails::getAdviceType)),
  TRANSFER_DATE(new Accessor<>(CivilClaimDetails::getTransferDate), FieldType.DATE),
  MEDICAL_REPORTS_CLAIMED(new Accessor<>(CivilClaimDetails::getMedicalReportsClaimed)),
  EXEMPTION_CRITERIA_SATISFIED(new Accessor<>(CivilClaimDetails::getExemptionCriteriaSatisfied)),
  IRC_SURGERY(new Accessor<>(CivilClaimDetails::getIsIrcSurgery)),
  SURGERY_DATE(new Accessor<>(CivilClaimDetails::getSurgeryDate), FieldType.DATE),
  SURGERY_CLIENTS_COUNT(new Accessor<>(CivilClaimDetails::getSurgeryClientsCount)),
  SURGERY_MATTERS_COUNT(new Accessor<>(CivilClaimDetails::getSurgeryMattersCount)),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      new Accessor<>(CivilClaimDetails::getMentalHealthTribunalReference)),
  IS_NRM_ADVICE(new Accessor<>(CivilClaimDetails::getIsNrmAdvice)),
  ;

  private final Accessor<?> accessor;
  private final FieldType type;

  CivilClaimDetailsViewField(Accessor<?> accessor) {
    this(accessor, FieldType.TEXT);
  }

  CivilClaimDetailsViewField(Accessor<?> accessor, FieldType type) {
    this.accessor = accessor;
    this.type = type;
  }

  public record Accessor<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldAccessor<CivilClaimDetails, T> {}
}
