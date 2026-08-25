package uk.gov.justice.laa.payments.amend.viewmodels.claimoverview;

import static uk.gov.justice.laa.payments.amend.viewmodels.claimoverview.ClaimOverviewView.putField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.AREA_OF_LAW;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.CASE_CONCLUDED_DATE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.CASE_START_DATE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.CATEGORY_OF_LAW;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.CLIENT_NAME;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.ESCAPED;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.FEE_CODE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.FEE_CODE_DESCRIPTION;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.OFFICE_CODE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.PROFIT_COST;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.PROVIDER_NAME;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.SUBMITTED_DATE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.TOTAL;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.UNIQUE_FILE_NUMBER;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.VAT;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.VAT_REQUESTED;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.asCrimeField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.MATTER_TYPE_CODE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.POLICE_STATION_COURT_PRISON_ID;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.SCHEME_ID;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.TRAVEL_COSTS;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.WAITING_COSTS;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimOverviewView(
    LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> summaryRows,
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> summaryClaimFieldRows,
    List<ClaimFieldRow> assessedTotals,
    List<ClaimFieldRow> allowedTotals,
    boolean hasAssessment)
    implements ClaimOverviewView {

  public CrimeClaimOverviewView(CrimeClaimDetails claim) {
    this(
        createSummaryRows(claim),
        createSummaryFields(claim),
        ClaimOverviewView.createAssessedTotals(claim),
        ClaimOverviewView.createAllowedTotals(claim),
        ClaimOverviewView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<CrimeClaimDetails>, Object> createSummaryRows(
      CrimeClaimDetails claim) {
    return toFieldMap(
        Stream.of(
            asCrimeField(CLIENT_NAME),
            asCrimeField(UNIQUE_FILE_NUMBER),
            asCrimeField(PROVIDER_NAME),
            asCrimeField(OFFICE_CODE),
            asCrimeField(SUBMITTED_DATE),
            asCrimeField(AREA_OF_LAW),
            asCrimeField(CATEGORY_OF_LAW),
            asCrimeField(FEE_CODE),
            asCrimeField(FEE_CODE_DESCRIPTION),
            POLICE_STATION_COURT_PRISON_ID,
            SCHEME_ID,
            MATTER_TYPE_CODE,
            asCrimeField(CASE_START_DATE),
            asCrimeField(CASE_CONCLUDED_DATE),
            asCrimeField(ESCAPED),
            asCrimeField(VAT_REQUESTED)),
        claim);
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createSummaryFields(
      CrimeClaimDetails claim) {
    var summaryFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(summaryFields, asCrimeField(FIXED_FEE), claim);
    putField(summaryFields, asCrimeField(PROFIT_COST), claim);
    putField(summaryFields, asCrimeField(DISBURSEMENTS), claim);
    putField(summaryFields, asCrimeField(DISBURSEMENTS_VAT), claim);
    putField(summaryFields, TRAVEL_COSTS, claim);
    putField(summaryFields, WAITING_COSTS, claim);
    putField(summaryFields, asCrimeField(VAT), claim);
    if (!claim.isHasAssessment()) {
      putField(summaryFields, asCrimeField(TOTAL), claim);
    }
    return summaryFields;
  }
}
