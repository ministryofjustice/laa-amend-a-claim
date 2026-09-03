package uk.gov.justice.laa.payments.amend.viewmodels.viewfield;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimPatch;
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
      ClaimPatch.Builder::clientForename,
      "client.clientForename"),
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClientDateOfBirth,
      ClaimPatch.Builder::clientDateOfBirth,
      "client.clientDateOfBirth"),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientPostcode,
      ClaimPatch.Builder::clientPostcode,
      "client.clientPostcode"),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueClientNumber,
      ClaimPatch.Builder::uniqueClientNumber,
      "client.uniqueClientNumber"),
  IS_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientLegallyAided,
      ClaimPatch.Builder::isLegallyAided,
      "client.isLegallyAided"),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientPostalApplicationAccepted,
      ClaimPatch.Builder::isPostalApplicationAccepted,
      "claimCase.isPostalApplicationAccepted"),

  // Client 2 fields
  CLIENT_2_FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Forename,
      ClaimPatch.Builder::client2Forename,
      "client.client2Forename"),
  CLIENT_2_SURNAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Surname,
      ClaimPatch.Builder::client2Surname,
      "client.client2Surname"),
  CLIENT_2_DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClient2DateOfBirth,
      ClaimPatch.Builder::client2DateOfBirth,
      "client.client2DateOfBirth"),
  CLIENT_2_UCN(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Ucn,
      ClaimPatch.Builder::client2Ucn,
      "client.client2Ucn"),
  CLIENT_2_POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Postcode,
      ClaimPatch.Builder::client2Postcode,
      "client.client2Postcode"),
  CLIENT_2_GENDER(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Gender,
      ClaimPatch.Builder::client2GenderCode,
      FieldOptions.GENDER,
      "client.client2GenderCode"),
  CLIENT_2_ETHNICITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Ethnicity,
      ClaimPatch.Builder::client2EthnicityCode,
      FieldOptions.ETHNICITY_CODE,
      "client.client2EthnicityCode"),
  CLIENT_2_DISABILITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Disability,
      ClaimPatch.Builder::client2DisabilityCode,
      FieldOptions.DISABILITY_CODE,
      "client.client2DisabilityCode"),
  IS_CLIENT_2_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2LegallyAided,
      ClaimPatch.Builder::client2IsLegallyAided,
      "client.client2IsLegallyAided"),
  IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2PostalApplicationAccepted,
      ClaimPatch.Builder::isClient2PostalApplicationAccepted,
      "claimCase.isClient2PostalApplicationAccepted"),

  // Case Type fields
  MATTER_TYPE_CODE_1(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType1,
      ClaimPatch.Builder::matterTypeCode,
      "claim.matterTypeCode") {
    @Override
    public Set<String> getAmendedFieldIdentifiers() {
      return Set.of(MatterTypeUtils.partIdentifier(MatterTypeUtils.FIRST_PART));
    }
  },
  MATTER_TYPE_CODE_2(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getMatterType2,
      ClaimPatch.Builder::matterTypeCode,
      "claim.matterTypeCode") {
    @Override
    public Set<String> getAmendedFieldIdentifiers() {
      return Set.of(MatterTypeUtils.partIdentifier(MatterTypeUtils.SECOND_PART));
    }
  },

  // Case Details fields
  CLAIM_ID(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getCaseId,
      ClaimPatch.Builder::caseId,
      "claimCase.caseId"),
  UNIQUE_CASE_ID(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueCaseId,
      ClaimPatch.Builder::uniqueCaseId,
      "claimCase.uniqueCaseId"),
  MEDIATION_SESSIONS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationSessionsCount,
      ClaimPatch.Builder::mediationSessionsCount,
      "claim.mediationSessionsCount"),
  MEDIATION_TIME_MINUTES(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationTimeMinutes,
      ClaimPatch.Builder::mediationTimeMinutes,
      "claim.mediationTimeMinutes"),
  OUTCOME(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getOutcome,
      ClaimPatch.Builder::outcomeCode,
      FieldOptions.MEDIATION_OUTCOME,
      "claimCase.outcomeCode"),
  OUTREACH_LOCATION(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getOutreachLocation,
      ClaimPatch.Builder::outreachLocation,
      "claim.outreachLocation"),
  REFERRAL_SOURCE(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getReferralSource,
      ClaimPatch.Builder::referralSource,
      FieldOptions.REFERRAL_SOURCE,
      "claim.referralSource"),
  SCHEDULE_REFERENCE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getScheduleReference,
      ClaimPatch.Builder::scheduleReference,
      "claim.scheduleReference"),
  CASE_CONCLUDED_DATE(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getCaseEndDate,
      ClaimPatch.Builder::caseConcludedDate,
      "claim.caseConcludedDate"),
  ;

  private final MediationClaimViewFieldGetter<?> getter;
  private final String claimsApiFieldName;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
      String claimsApiFieldName) {
    this(fieldType, patchType, getter, patcher, List.of(), claimsApiFieldName);
  }

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<ClaimPatch.Builder, T, ClaimPatch.Builder> patcher,
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
