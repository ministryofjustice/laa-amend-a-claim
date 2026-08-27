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
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.asMediationField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField.toFieldMap;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField.MATTER_TYPE_CODE_1;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField.MATTER_TYPE_CODE_2;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record MediationClaimOverviewView(
    LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> summaryRows,
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> summaryClaimFieldRows,
    List<ClaimFieldRow> assessedTotals,
    List<ClaimFieldRow> allowedTotals,
    boolean hasAssessment)
    implements ClaimOverviewView {

  public MediationClaimOverviewView(MediationClaimDetails claim) {
    this(
        createSummaryRows(claim),
        createSummaryFields(claim),
        ClaimOverviewView.createAssessedTotals(claim),
        ClaimOverviewView.createAllowedTotals(claim),
        ClaimOverviewView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<MediationClaimDetails>, Object> createSummaryRows(
      MediationClaimDetails claim) {
    return toFieldMap(
        Stream.of(
            asMediationField(CLIENT_NAME),
            asMediationField(UNIQUE_FILE_NUMBER),
            UNIQUE_CLIENT_NUMBER,
            asMediationField(PROVIDER_NAME),
            asMediationField(OFFICE_CODE),
            asMediationField(SUBMITTED_DATE),
            asMediationField(AREA_OF_LAW),
            asMediationField(CATEGORY_OF_LAW),
            asMediationField(FEE_CODE),
            asMediationField(FEE_CODE_DESCRIPTION),
            MATTER_TYPE_CODE_1,
            MATTER_TYPE_CODE_2,
            asMediationField(CASE_START_DATE),
            asMediationField(CASE_CONCLUDED_DATE),
            asMediationField(ESCAPED),
            asMediationField(VAT_REQUESTED)),
        claim);
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createSummaryFields(
      MediationClaimDetails claim) {
    var summaryFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(summaryFields, asMediationField(FIXED_FEE), claim);
    putField(summaryFields, asMediationField(PROFIT_COST), claim);
    putField(summaryFields, asMediationField(DISBURSEMENTS), claim);
    putField(summaryFields, asMediationField(DISBURSEMENTS_VAT), claim);
    putField(summaryFields, asMediationField(VAT), claim);
    if (!claim.isHasAssessment()) {
      putField(summaryFields, asMediationField(TOTAL), claim);
    }
    return summaryFields;
  }
}
