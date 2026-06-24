package uk.gov.justice.laa.amend.claim.viewmodels.claimcase;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.DSCC_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.FEE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.IS_DUTY_SOLICITOR;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.IS_YOUTH_COURT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.MAAT_ID;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.MATTER_TYPE_CODE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.OUTCOME_FOR_CLIENT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.POLICE_STATION_COURT_ATTENDANCES_COUNT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.PRISON_LAW_PRIOR_APPROVAL_NUMBER;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.REPRESENTATION_ORDER_DATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.SCHEME_ID;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.STAGE_REACHED;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.STANDARD_FEE_CATEGORY;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.SUSPECTS_DEFENDANTS_COUNT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.UNIQUE_FILE_NUMBER;

import java.util.LinkedHashMap;
import java.util.stream.Stream;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimCaseView(
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> caseTypeRows,
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> caseDetailsRows)
    implements ClaimCaseView<ClaimViewField<CrimeClaimDetails>> {

  public CrimeClaimCaseView(CrimeClaimDetails claim) {
    this(createCaseTypeRows(claim), createCaseDetailsRows(claim));
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createCaseTypeRows(
      CrimeClaimDetails claim) {
    Stream<ClaimViewField<CrimeClaimDetails>> fields = Stream.of(FEE_CODE, MATTER_TYPE_CODE);

    return toFieldMap(fields, claim);
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createCaseDetailsRows(
      CrimeClaimDetails claim) {
    Stream<ClaimViewField<CrimeClaimDetails>> fields =
        Stream.of(
            STAGE_REACHED,
            UNIQUE_FILE_NUMBER,
            REPRESENTATION_ORDER_DATE,
            CASE_CONCLUDED_DATE,
            STANDARD_FEE_CATEGORY,
            OUTCOME_FOR_CLIENT,
            SUSPECTS_DEFENDANTS_COUNT,
            POLICE_STATION_COURT_ATTENDANCES_COUNT,
            POLICE_STATION_COURT_PRISON_ID,
            SCHEME_ID,
            DSCC_NUMBER,
            MAAT_ID,
            PRISON_LAW_PRIOR_APPROVAL_NUMBER,
            IS_DUTY_SOLICITOR,
            IS_YOUTH_COURT);

    return toFieldMap(fields, claim);
  }
}
