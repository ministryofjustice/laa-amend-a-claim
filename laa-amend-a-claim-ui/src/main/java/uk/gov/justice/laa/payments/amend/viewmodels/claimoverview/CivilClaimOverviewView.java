package uk.gov.justice.laa.payments.amend.viewmodels.claimoverview;

import static uk.gov.justice.laa.payments.amend.viewmodels.claimoverview.ClaimOverviewView.putField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.CMRH_ORAL;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.CMRH_TELEPHONE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.COUNSELS_COST;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.DETENTION_TRAVEL;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.HOME_OFFICE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.JR_FORM_FILLING;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_1;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.MATTER_TYPE_CODE_2;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER;
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
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.asCivilField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.toFieldMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.payments.amend.models.CivilClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record CivilClaimOverviewView(
    LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> summaryRows,
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> summaryClaimFieldRows,
    List<ClaimFieldRow> assessedTotals,
    List<ClaimFieldRow> allowedTotals,
    boolean hasAssessment)
    implements ClaimOverviewView {

  public CivilClaimOverviewView(CivilClaimDetails claim) {
    this(
        createSummaryRows(claim),
        createSummaryFields(claim),
        ClaimOverviewView.createAssessedTotals(claim),
        ClaimOverviewView.createAllowedTotals(claim),
        ClaimOverviewView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<CivilClaimDetails>, Object> createSummaryRows(
      CivilClaimDetails claim) {
    return toFieldMap(
        Stream.of(
            asCivilField(CLIENT_NAME),
            asCivilField(UNIQUE_FILE_NUMBER),
            UNIQUE_CLIENT_NUMBER,
            asCivilField(PROVIDER_NAME),
            asCivilField(OFFICE_CODE),
            asCivilField(SUBMITTED_DATE),
            asCivilField(AREA_OF_LAW),
            asCivilField(CATEGORY_OF_LAW),
            asCivilField(FEE_CODE),
            asCivilField(FEE_CODE_DESCRIPTION),
            MATTER_TYPE_CODE_1,
            MATTER_TYPE_CODE_2,
            asCivilField(CASE_START_DATE),
            asCivilField(CASE_CONCLUDED_DATE),
            asCivilField(ESCAPED),
            asCivilField(VAT_REQUESTED)),
        claim);
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createSummaryFields(
      CivilClaimDetails claim) {
    var summaryFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(summaryFields, asCivilField(FIXED_FEE), claim);
    putField(summaryFields, asCivilField(PROFIT_COST), claim);
    putField(summaryFields, asCivilField(DISBURSEMENTS), claim);
    putField(summaryFields, asCivilField(DISBURSEMENTS_VAT), claim);
    putField(summaryFields, DETENTION_TRAVEL, claim);
    putField(summaryFields, JR_FORM_FILLING, claim);
    putField(summaryFields, COUNSELS_COST, claim);
    putField(summaryFields, CMRH_ORAL, claim);
    putField(summaryFields, CMRH_TELEPHONE, claim);
    putField(summaryFields, HOME_OFFICE, claim);
    putField(summaryFields, SUBSTANTIVE_HEARING, claim);
    putField(summaryFields, ADJOURNED_HEARING_FEE, claim);
    putField(summaryFields, asCivilField(VAT), claim);
    if (!claim.isHasAssessment()) {
      putField(summaryFields, asCivilField(TOTAL), claim);
    }
    return summaryFields;
  }
}
