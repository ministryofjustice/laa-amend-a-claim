package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CASE_REFERENCE_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CASE_START_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.CLAIM_ID;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.FEE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.MATTER_TYPE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.MEDIATION_SESSIONS_COUNT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.MEDIATION_TIME_MINUTES;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.OUTCOME;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.OUTREACH_LOCATION;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.REFERRAL_SOURCE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.SCHEDULE_REFERENCE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField.UNIQUE_CASE_ID;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record MediationClaimCaseView(
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<MediationClaimDetails>> {

  public MediationClaimCaseView(MediationClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createCaseTypeRows(
      MediationClaimDetails claim) {
    Stream<ClaimViewField<MediationClaimDetails>> fields = Stream.of(FEE_CODE, MATTER_TYPE_CODE);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createCaseDetailsRows(
      MediationClaimDetails claim) {

    Stream<ClaimViewField<MediationClaimDetails>> fields =
        Stream.of(
            CASE_REFERENCE_NUMBER,
            CASE_START_DATE,
            CLAIM_ID,
            UNIQUE_CASE_ID,
            CASE_CONCLUDED_DATE,
            MEDIATION_SESSIONS_COUNT,
            MEDIATION_TIME_MINUTES,
            OUTCOME,
            OUTREACH_LOCATION,
            REFERRAL_SOURCE,
            SCHEDULE_REFERENCE);

    return toFieldMap(fields, claim);
  }
}
