package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;

public record CrimeClaimCaseView(
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<CrimeClaimDetails>> {

  public CrimeClaimCaseView(CrimeClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createCaseTypeRows(
      CrimeClaimDetails claim) {
    Stream<ClaimViewField<CrimeClaimDetails>> fields =
        Stream.of(CrimeClaimDetailsViewField.FEE_CODE, CrimeClaimDetailsViewField.MATTER_TYPE_CODE);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createCaseDetailsRows(
      CrimeClaimDetails claim) {
    Stream<ClaimViewField<CrimeClaimDetails>> fields =
        Stream.of(
            CrimeClaimDetailsViewField.STAGE_REACHED,
            CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER,
            CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE,
            CrimeClaimDetailsViewField.CASE_END_DATE,
            CrimeClaimDetailsViewField.STANDARD_FEE_CATEGORY,
            CrimeClaimDetailsViewField.OUTCOME_FOR_CLIENT,
            CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT,
            CrimeClaimDetailsViewField.POLICE_STATION_COURT_ATTENDANCES_COUNT,
            CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID,
            CrimeClaimDetailsViewField.SCHEME_ID,
            CrimeClaimDetailsViewField.DSCC_NUMBER,
            CrimeClaimDetailsViewField.MAAT_ID,
            CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER,
            CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR,
            CrimeClaimDetailsViewField.IS_YOUTH_COURT);

    return toFieldMap(fields, claim);
  }
}
