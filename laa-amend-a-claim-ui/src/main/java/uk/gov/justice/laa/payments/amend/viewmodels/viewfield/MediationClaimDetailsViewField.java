package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;
import uk.gov.justice.laa.payments.amend.utils.MatterTypeUtils;

@Getter
public enum MediationClaimDetailsViewField implements ClaimViewField<MediationClaimDetails> {

  // Client fields
  FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientForename,
      Builder::clientForename,
      "client_forename"),
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClientDateOfBirth,
      Builder::clientDateOfBirth,
      "client_date_of_birth"),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientPostcode,
      Builder::clientPostcode,
      "client_postcode"),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueClientNumber,
      Builder::uniqueClientNumber,
      "unique_client_number"),
  IS_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientLegallyAided,
      Builder::isLegallyAided,
      "is_legally_aided"),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientPostalApplicationAccepted,
      Builder::isPostalApplicationAccepted,
      "is_postal_application_accepted"),

  // Client 2 fields
  CLIENT_2_FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Forename,
      Builder::client2Forename,
      "client2_forename"),
  CLIENT_2_SURNAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Surname,
      Builder::client2Surname,
      "client2_surname"),
  CLIENT_2_DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClient2DateOfBirth,
      Builder::client2DateOfBirth,
      "client2_date_of_birth"),
  CLIENT_2_UCN(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Ucn,
      Builder::client2Ucn,
      "client2_ucn"),
  CLIENT_2_POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Postcode,
      Builder::client2Postcode,
      "client2_postcode"),
  CLIENT_2_GENDER(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Gender,
      Builder::client2GenderCode,
      FieldOptions.GENDER,
      "client2_gender_code"),
  CLIENT_2_ETHNICITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Ethnicity,
      Builder::client2EthnicityCode,
      FieldOptions.ETHNICITY_CODE,
      "client2_ethnicity_code"),
  CLIENT_2_DISABILITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Disability,
      Builder::client2DisabilityCode,
      FieldOptions.DISABILITY_CODE,
      "client2_disability_code"),
  IS_CLIENT_2_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2LegallyAided,
      Builder::client2IsLegallyAided,
      "client2_is_legally_aided"),
  IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2PostalApplicationAccepted,
      Builder::isClient2PostalApplicationAccepted,
      "is_client2_postal_application_accepted"),

  // Case Type fields
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType1,
      Builder::matterTypeCode,
      MatterTypeUtils.MATTER_TYPE_CODE_1),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType2,
      Builder::matterTypeCode,
      MatterTypeUtils.MATTER_TYPE_CODE_2),

  // Case Details fields
  CLAIM_ID(
      FieldType.TEXT, String.class, MediationClaimDetails::getCaseId, Builder::caseId, "case_id"),
  UNIQUE_CASE_ID(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueCaseId,
      Builder::uniqueCaseId,
      "unique_case_id"),
  MEDIATION_SESSIONS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationSessionsCount,
      Builder::mediationSessionsCount,
      "mediation_sessions_count"),
  MEDIATION_TIME_MINUTES(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationTimeMinutes,
      Builder::mediationTimeMinutes,
      "mediation_time_minutes"),
  OUTCOME(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.MEDIATION_OUTCOME,
      "outcome_code"),
  OUTREACH_LOCATION(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getOutreachLocation,
      Builder::outreachLocation,
      "outreach_location"),
  REFERRAL_SOURCE(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getReferralSource,
      Builder::referralSource,
      FieldOptions.REFERRAL_SOURCE,
      "referral_source"),
  SCHEDULE_REFERENCE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getScheduleReference,
      Builder::scheduleReference,
      "schedule_reference"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getCaseEndDate,
      Builder::caseConcludedDate,
      "case_concluded_date");

  private final MediationClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, NO_OPTIONS, claimsApiFieldName);
  }

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options,
      String claimsApiFieldName) {
    this.getter = new MediationClaimViewFieldGetter<>(getter);
    this.claimsApiFieldName = claimsApiFieldName;
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
  }

  public record MediationClaimViewFieldGetter<T>(Function<MediationClaimDetails, T> getter)
      implements ClaimViewFieldGetter<MediationClaimDetails, T> {}
}
