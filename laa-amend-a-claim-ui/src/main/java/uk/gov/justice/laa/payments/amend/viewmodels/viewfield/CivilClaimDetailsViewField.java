package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.Amendability;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.utils.MatterTypeUtils;

@Getter
public enum CivilClaimDetailsViewField implements ClaimViewField<CivilClaimDetails> {
  // Client fields
  FORENAME(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getClientForename,
      Builder::clientForename,
      "client_forename"),
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getClientDateOfBirth,
      Builder::clientDateOfBirth,
      Amendability.UNTIL_ASSESSED,
      "client_date_of_birth"),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getClientPostcode,
      Builder::clientPostcode,
      "client_postcode"),
  IS_ELIGIBLE_CLIENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsEligibleClient,
      Builder::isEligibleClient,
      "is_eligible_client"),
  CLIENT_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getClientType,
      Builder::clientTypeCode,
      FieldOptions.CLIENT_TYPE,
      "client_type_code"),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueClientNumber,
      Builder::uniqueClientNumber,
      "unique_client_number"),
  HOME_OFFICE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getHomeOfficeClientNumber,
      Builder::homeOfficeClientNumber,
      "home_office_client_number"),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsPostalApplication,
      Builder::isPostalApplicationAccepted,
      "is_postal_application_accepted"),

  // Case type fields
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType1,
      Builder::matterTypeCode,
      MatterTypeUtils.MATTER_TYPE_CODE_1),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMatterType2,
      Builder::matterTypeCode,
      MatterTypeUtils.MATTER_TYPE_CODE_2),

  // Case details fields
  STAGE_REACHED(
      FieldType.TEXT,
      String.class,
      ClaimDetails::getStageReached,
      Builder::stageReachedCode,
      "stage_reached_code"),
  SCHEDULE_REFERENCE_CIVIL(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getScheduleReference,
      Builder::scheduleReference,
      "schedule_reference"),
  CASE_ID(FieldType.TEXT, String.class, CivilClaimDetails::getCaseId, Builder::caseId, "case_id"),
  UNIQUE_CASE_ID(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueCaseId,
      Builder::uniqueCaseId,
      "unique_case_id"),
  CASE_CONCLUDED_CLAIMED_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getCaseEndDate,
      Builder::caseConcludedDate,
      Amendability.UNTIL_ASSESSED,
      "case_concluded_date"),
  UNIQUE_FILE_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getUniqueFileNumber,
      Builder::uniqueFileNumber,
      "unique_file_number"),
  CASE_STAGE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getCaseStage,
      Builder::caseStageCode,
      FieldOptions.CASE_STAGE,
      "case_stage_code"),
  VALUE_OF_COSTS(
      FieldType.MONETARY,
      BigDecimal.class,
      CivilClaimDetails::getValueOfCosts,
      Builder::costsDamagesRecoveredAmount,
      "costs_damages_recovered_amount"),
  PROCUREMENT_AREA(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getProcurementArea,
      Builder::procurementAreaCode,
      "procurement_area_code"),
  ACCESS_POINT(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getAccessPoint,
      Builder::accessPointCode,
      "access_point_code"),
  OUTCOME_FOR_CLIENT(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getOutcome,
      Builder::outcomeCode,
      "outcome_code"),
  EXCEPTIONAL_CASE_FUNDING(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getExceptionalCaseFundingReference,
      Builder::exceptionalCaseFundingReference,
      "exceptional_case_funding_reference"),
  CIVIL_LEGAL_ADVICE_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceReference,
      Builder::claReferenceNumber,
      "cla_reference_number"),
  CIVIL_LEGAL_ADVICE_EXEMPTION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCivilLegalAdviceExemption,
      Builder::claExemptionCode,
      "cla_exemption_code"),
  DELIVERY_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getDeliveryLocation,
      Builder::deliveryLocation,
      "delivery_location"),
  COURT_LOCATION(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getCourtLocation,
      Builder::courtLocationCode,
      "court_location_code"),
  AIT_HEARING_CENTRE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAitHearingCentre,
      Builder::aitHearingCentreCode,
      FieldOptions.AIT_HEARING_CENTRE,
      "ait_hearing_centre_code"),
  LOCAL_AUTHORITY_NUMBER(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getLocalAuthorityNumber,
      Builder::localAuthorityNumber,
      "local_authority_number"),
  DESIGNATED_ACCREDITED_REPRESENTATIVE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getDesignatedAccreditedRepresentative,
      Builder::designatedAccreditedRepresentativeCode,
      FieldOptions.DESIGNATED_ACCREDITED_REPRESENTATIVE,
      "designated_accredited_representative_code"),
  ADVICE_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getAdviceTime,
      Builder::adviceTime,
      "advice_time"),
  TRAVEL_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getTravelTime,
      Builder::travelTime,
      "travel_time"),
  WAITING_TIME(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getWaitingTime,
      Builder::waitingTime,
      "waiting_time"),
  ADDITIONAL_TRAVEL_PAYMENT(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsAdditionalTravelPayment,
      Builder::isAdditionalTravelPayment,
      "is_additional_travel_payment"),
  FOLLOW_ON_WORK(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getFollowOnWork,
      Builder::followOnWork,
      "follow_on_work"),
  TOLERANCE_INDICATOR(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsToleranceApplicable,
      Builder::isToleranceApplicable,
      "is_tolerance_applicable"),
  LEGACY_CASE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLegacyCase,
      Builder::isLegacyCase,
      "is_legacy_case"),
  MEETINGS_ATTENDED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getMeetingsAttended,
      Builder::meetingsAttendedCode,
      FieldOptions.MEETINGS_ATTENDED,
      "meetings_attended_code"),
  ADVICE_TYPE(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getAdviceType,
      Builder::adviceTypeCode,
      FieldOptions.ADVICE_TYPE,
      "advice_type_code"),
  TRANSFER_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getTransferDate,
      Builder::transferDate,
      "transfer_date"),
  MEDICAL_REPORTS_CLAIMED(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getMedicalReportsClaimed,
      Builder::medicalReportsCount,
      "medical_reports_count"),
  EXEMPTION_CRITERIA_SATISFIED(
      FieldType.ENUM,
      String.class,
      CivilClaimDetails::getExemptionCriteriaSatisfied,
      Builder::exemptionCriteriaSatisfied,
      FieldOptions.EXEMPTION_CRITERIA_SATISFIED,
      "exemption_criteria_satisfied"),
  IRC_SURGERY(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsIrcSurgery,
      Builder::isIrcSurgery,
      "is_irc_surgery"),
  SURGERY_DATE(
      FieldType.DATE,
      String.class,
      CivilClaimDetails::getSurgeryDate,
      Builder::surgeryDate,
      "surgery_date"),
  SURGERY_CLIENTS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryClientsCount,
      Builder::surgeryClientsCount,
      "surgery_clients_count"),
  SURGERY_MATTERS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      CivilClaimDetails::getSurgeryMattersCount,
      Builder::surgeryMattersCount,
      "surgery_matters_count"),
  MENTAL_HEALTH_TRIBUNAL_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getMentalHealthTribunalReference,
      Builder::mentalHealthTribunalReference,
      "mental_health_tribunal_reference"),
  IS_NRM_ADVICE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsNrmAdvice,
      Builder::isNrmAdvice,
      "is_nrm_advice"),

  // Cost fields
  COUNSELS_COST(
      FieldType.MONETARY,
      FieldType.MONETARY,
      BigDecimal.class,
      CivilClaimDetails::getCounselsCost,
      Builder::netCounselCostsAmount,
      Amendability.UNTIL_ASSESSED,
      "net_counsel_costs_amount",
      "net_cost_of_counsel_amount"),
  TRAVEL_AND_WAITING_COSTS(
      FieldType.MONETARY,
      FieldType.MONETARY,
      BigDecimal.class,
      CivilClaimDetails::getTravelAndWaitingCosts,
      Builder::travelWaitingCostsAmount,
      Amendability.UNTIL_ASSESSED,
      "travel_waiting_costs_amount",
      "travel_and_waiting_costs_amount"),
  DETENTION_TRAVEL(
      FieldType.MONETARY,
      FieldType.MONETARY,
      BigDecimal.class,
      CivilClaimDetails::getDetentionTravelWaitingCosts,
      Builder::detentionTravelWaitingCostsAmount,
      Amendability.UNTIL_ASSESSED,
      "detention_travel_waiting_costs_amount",
      "detention_travel_and_waiting_costs_amount"),
  JR_FORM_FILLING(
      FieldType.MONETARY,
      FieldType.MONETARY,
      BigDecimal.class,
      CivilClaimDetails::getJrFormFillingCost,
      Builder::jrFormFillingAmount,
      Amendability.UNTIL_ASSESSED,
      "jr_form_filling_amount",
      "jr_form_filling_amount"),
  ADJOURNED_HEARING_FEE(
      FieldType.NUMBER,
      FieldType.MONETARY,
      Integer.class,
      CivilClaimDetails::getAdjournedHearing,
      Builder::adjournedHearingFeeAmount,
      Amendability.UNTIL_ASSESSED,
      "adjourned_hearing_fee_amount",
      "bolt_on_adjourned_hearing_fee"),
  CMRH_TELEPHONE(
      FieldType.NUMBER,
      FieldType.MONETARY,
      Integer.class,
      CivilClaimDetails::getCmrhTelephone,
      Builder::cmrhTelephoneCount,
      Amendability.UNTIL_ASSESSED,
      "cmrh_telephone_count",
      "bolt_on_cmrh_telephone_fee"),
  CMRH_ORAL(
      FieldType.NUMBER,
      FieldType.MONETARY,
      Integer.class,
      CivilClaimDetails::getCmrhOral,
      Builder::cmrhOralCount,
      Amendability.UNTIL_ASSESSED,
      "cmrh_oral_count",
      "bolt_on_cmrh_oral_fee"),
  HOME_OFFICE(
      FieldType.NUMBER,
      FieldType.MONETARY,
      Integer.class,
      CivilClaimDetails::getHoInterview,
      Builder::hoInterview,
      Amendability.UNTIL_ASSESSED,
      "ho_interview",
      "bolt_on_home_office_interview_fee"),
  SUBSTANTIVE_HEARING(
      FieldType.BOOLEAN,
      FieldType.MONETARY,
      Boolean.class,
      CivilClaimDetails::getSubstantiveHearing,
      Builder::isSubstantiveHearing,
      Amendability.UNTIL_ASSESSED,
      "is_substantive_hearing",
      "bolt_on_substantive_hearing_fee"),
  BOLT_ON_TOTAL_FEE(
      FieldType.MONETARY,
      FieldType.MONETARY,
      NO_PATCH_TYPE,
      NO_CIVIL_GETTER,
      NO_PATCHER,
      Amendability.NEVER,
      NO_CLAIMS_API_FIELD_NAME,
      "bolt_on_total_fee_amount"),
  IS_LONDON_RATE(
      FieldType.BOOLEAN,
      Boolean.class,
      CivilClaimDetails::getIsLondonRate,
      (Builder builder, Boolean isLondonRate) -> builder.isLondonRate(isLondonRate),
      Amendability.UNTIL_ASSESSED,
      "is_london_rate"),
  PRIOR_AUTHORITY_REFERENCE(
      FieldType.TEXT,
      String.class,
      CivilClaimDetails::getPriorAuthorityReference,
      Builder::priorAuthorityReference,
      Amendability.UNTIL_ASSESSED,
      "prior_authority_reference");

  private final CivilClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final String feeApiFieldName;
  private final FieldType fieldType;
  private final FieldType feeFieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final Amendability amendability;
  private final List<FieldOption> options;

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      String claimsApiFieldName) {
    this(
        fieldType,
        NO_FEE_API_TYPE,
        patchType,
        getter,
        patcher,
        List.of(),
        Amendability.ALWAYS,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options,
      String claimsApiFieldName) {
    this(
        fieldType,
        NO_FEE_API_TYPE,
        patchType,
        getter,
        patcher,
        options,
        Amendability.ALWAYS,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName) {
    this(
        fieldType,
        NO_FEE_API_TYPE,
        patchType,
        getter,
        patcher,
        List.of(),
        amendability,
        claimsApiFieldName,
        NO_FEE_API_FIELD_NAME);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      FieldType feeFieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      Amendability amendability,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this(
        fieldType,
        feeFieldType,
        patchType,
        getter,
        patcher,
        List.of(),
        amendability,
        claimsApiFieldName,
        feeApiFieldName);
  }

  <T> CivilClaimDetailsViewField(
      FieldType fieldType,
      FieldType feeFieldType,
      Class<T> patchType,
      Function<CivilClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options,
      Amendability amendability,
      String claimsApiFieldName,
      String feeApiFieldName) {
    this.getter = new CivilClaimViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.feeApiFieldName = feeApiFieldName;
    this.fieldType = fieldType;
    this.feeFieldType = feeFieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
    this.amendability = amendability;
  }

  public record CivilClaimViewFieldGetter<T>(Function<CivilClaimDetails, T> getter)
      implements ClaimViewFieldGetter<CivilClaimDetails, T> {}
}
