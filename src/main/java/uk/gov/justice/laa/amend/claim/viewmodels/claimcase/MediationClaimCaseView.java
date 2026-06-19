package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

public record MediationClaimCaseView(
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<MediationClaimDetails>> {

  public MediationClaimCaseView(MediationClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createCaseTypeRows(
      MediationClaimDetails claim) {
    Stream<ClaimViewField<MediationClaimDetails>> fields =
        Stream.of(
            MediationClaimDetailsViewField.FEE_CODE,
            MediationClaimDetailsViewField.MATTER_TYPE_CODE_1,
            MediationClaimDetailsViewField.MATTER_TYPE_CODE_2);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createCaseDetailsRows(
      MediationClaimDetails claim) {

    Stream<ClaimViewField<MediationClaimDetails>> fields =
        Stream.of(
            MediationClaimDetailsViewField.CASE_REFERENCE_NUMBER,
            MediationClaimDetailsViewField.CASE_START_DATE,
            MediationClaimDetailsViewField.CLAIM_ID,
            MediationClaimDetailsViewField.UNIQUE_CASE_ID,
            MediationClaimDetailsViewField.CASE_CONCLUDED_DATE,
            MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT,
            MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES,
            MediationClaimDetailsViewField.OUTCOME,
            MediationClaimDetailsViewField.OUTREACH_LOCATION,
            MediationClaimDetailsViewField.REFERRAL_SOURCE,
            MediationClaimDetailsViewField.SCHEDULE_REFERENCE);

    return toFieldMap(fields, claim);
  }
}
