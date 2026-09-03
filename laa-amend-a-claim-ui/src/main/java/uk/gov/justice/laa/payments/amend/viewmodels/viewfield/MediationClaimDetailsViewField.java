package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.models.enums.FieldType;

@Getter
public enum MediationClaimDetailsViewField implements ClaimViewField<MediationClaimDetails> {

  // Client fields
  FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientForename,
      Builder::clientForename,
      "client.clientForename"),
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClientDateOfBirth,
      Builder::clientDateOfBirth,
      "client.clientDateOfBirth"),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientPostcode,
      Builder::clientPostcode,
      "client.clientPostcode"),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueClientNumber,
      Builder::uniqueClientNumber,
      "client.uniqueClientNumber"),
  IS_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientLegallyAided,
      Builder::isLegallyAided,
      "client.isLegallyAided"),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientPostalApplicationAccepted,
      Builder::isPostalApplicationAccepted,
      "claimCase.isPostalApplicationAccepted"),

  // Client 2 fields
  CLIENT_2_FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Forename,
      Builder::client2Forename,
      "client.client2Forename"),
  CLIENT_2_SURNAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Surname,
      Builder::client2Surname,
      "client.client2Surname"),
  CLIENT_2_DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClient2DateOfBirth,
      Builder::client2DateOfBirth,
      "client.client2DateOfBirth"),
  CLIENT_2_UCN(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Ucn,
      Builder::client2Ucn,
      "client.client2Ucn"),
  CLIENT_2_POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Postcode,
      Builder::client2Postcode,
      "client.client2Postcode"),
  CLIENT_2_GENDER(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Gender,
      Builder::client2GenderCode,
      FieldOptions.GENDER,
      "client.client2GenderCode"),
  CLIENT_2_ETHNICITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Ethnicity,
      Builder::client2EthnicityCode,
      FieldOptions.ETHNICITY_CODE,
      "client.client2EthnicityCode"),
  CLIENT_2_DISABILITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Disability,
      Builder::client2DisabilityCode,
      FieldOptions.DISABILITY_CODE,
      "client.client2DisabilityCode"),
  IS_CLIENT_2_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2LegallyAided,
      Builder::client2IsLegallyAided,
      "client.client2IsLegallyAided"),
  IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2PostalApplicationAccepted,
      Builder::isClient2PostalApplicationAccepted,
      "claimCase.isClient2PostalApplicationAccepted"),

  // Case Type fields
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType1,
      Builder::matterTypeCode,
      "claim.matterTypeCode"),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType2,
      Builder::matterTypeCode,
      "claim.matterTypeCode"),

  // Case Details fields
  CLAIM_ID(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getCaseId,
      Builder::caseId,
      "claimCase.caseId"),
  UNIQUE_CASE_ID(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueCaseId,
      Builder::uniqueCaseId,
      "claimCase.uniqueCaseId"),
  MEDIATION_SESSIONS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationSessionsCount,
      Builder::mediationSessionsCount,
      "claim.mediationSessionsCount"),
  MEDIATION_TIME_MINUTES(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationTimeMinutes,
      Builder::mediationTimeMinutes,
      "claim.mediationTimeMinutes"),
  OUTCOME(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.MEDIATION_OUTCOME,
      "claimCase.outcomeCode"),
  OUTREACH_LOCATION(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getOutreachLocation,
      Builder::outreachLocation,
      "claim.outreachLocation"),
  REFERRAL_SOURCE(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getReferralSource,
      Builder::referralSource,
      FieldOptions.REFERRAL_SOURCE,
      "claim.referralSource"),
  SCHEDULE_REFERENCE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getScheduleReference,
      Builder::scheduleReference,
      "claim.scheduleReference"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getCaseEndDate,
      Builder::caseConcludedDate,
      "claim.caseConcludedDate");

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
