package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimAmendmentPatch.Builder;

@Getter
public enum MediationClaimDetailsViewField implements ClaimViewField<MediationClaimDetails> {

  // Client fields
  DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClientDateOfBirth,
      Builder::clientDateOfBirth),
  POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClientPostcode,
      Builder::clientPostcode),
  UNIQUE_CLIENT_NUMBER(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getUniqueClientNumber,
      Builder::uniqueClientNumber),
  IS_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientLegallyAided,
      Builder::isLegallyAided),
  IS_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClientPostalApplicationAccepted,
      Builder::isPostalApplicationAccepted),

  // Client 2 fields
  CLIENT_2_FORENAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Forename,
      Builder::client2Forename),
  CLIENT_2_SURNAME(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Surname,
      Builder::client2Surname),
  CLIENT_2_DATE_OF_BIRTH(
      FieldType.DATE,
      String.class,
      MediationClaimDetails::getClient2DateOfBirth,
      Builder::client2DateOfBirth),
  CLIENT_2_UCN(
      FieldType.TEXT, String.class, MediationClaimDetails::getClient2Ucn, Builder::client2Ucn),
  CLIENT_2_POSTCODE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getClient2Postcode,
      Builder::client2Postcode),
  CLIENT_2_GENDER(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Gender,
      Builder::client2GenderCode,
      FieldOptions.GENDER),
  CLIENT_2_ETHNICITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Ethnicity,
      Builder::client2EthnicityCode,
      FieldOptions.ETHNICITY_CODE),
  CLIENT_2_DISABILITY(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getClient2Disability,
      Builder::client2DisabilityCode,
      FieldOptions.DISABILITY_CODE),
  IS_CLIENT_2_LEGALLY_AIDED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2LegallyAided,
      Builder::client2IsLegallyAided),
  IS_CLIENT_2_POSTAL_APPLICATION_ACCEPTED(
      FieldType.BOOLEAN,
      Boolean.class,
      MediationClaimDetails::getIsClient2PostalApplicationAccepted,
      Builder::isClient2PostalApplicationAccepted),

  // Case Type fields
  FEE_CODE(FieldType.TEXT, String.class, MediationClaimDetails::getFeeCode, Builder::feeCode),
  MATTER_TYPE_CODE_1(
      FieldType.TEXT, String.class, MediationClaimDetails::getMatterType1, Builder::matterTypeCode),
  MATTER_TYPE_CODE_2(
      FieldType.TEXT, String.class, MediationClaimDetails::getMatterType2, Builder::matterTypeCode),

  // Case Details fields
  CLAIM_ID(FieldType.TEXT, String.class, MediationClaimDetails::getCaseId, Builder::caseId),
  UNIQUE_CASE_ID(
      FieldType.TEXT, String.class, MediationClaimDetails::getUniqueCaseId, Builder::uniqueCaseId),
  MEDIATION_SESSIONS_COUNT(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationSessionsCount,
      Builder::mediationSessionsCount),
  MEDIATION_TIME_MINUTES(
      FieldType.NUMBER,
      Integer.class,
      MediationClaimDetails::getMediationTimeMinutes,
      Builder::mediationTimeMinutes),
  OUTCOME(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getOutcome,
      Builder::outcomeCode,
      FieldOptions.OUTCOME),
  OUTREACH_LOCATION(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getOutreachLocation,
      Builder::outreachLocation),
  REFERRAL_SOURCE(
      FieldType.ENUM,
      String.class,
      MediationClaimDetails::getReferralSource,
      Builder::referralSource,
      FieldOptions.REFERRAL_SOURCE),
  SCHEDULE_REFERENCE(
      FieldType.TEXT,
      String.class,
      MediationClaimDetails::getScheduleReference,
      Builder::scheduleReference),
  ;

  private final MediationClaimViewFieldGetter<?> getter;
  private final FieldType fieldType;
  private final ClaimViewFieldPatcher<?> patcher;
  private final List<FieldOption> options;

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher) {
    this(fieldType, patchType, getter, patcher, List.of());
  }

  <T> MediationClaimDetailsViewField(
      FieldType fieldType,
      Class<T> patchType,
      Function<MediationClaimDetails, ?> getter,
      BiFunction<Builder, T, Builder> patcher,
      List<FieldOption> options) {
    this.getter = new MediationClaimViewFieldGetter<>(getter);
    this.fieldType = fieldType;
    this.patcher = new ClaimViewFieldPatcher<>(patchType, patcher);
    this.options = List.copyOf(options);
  }

  public record MediationClaimViewFieldGetter<T>(Function<MediationClaimDetails, T> getter)
      implements ClaimViewFieldGetter<MediationClaimDetails, T> {}
}
