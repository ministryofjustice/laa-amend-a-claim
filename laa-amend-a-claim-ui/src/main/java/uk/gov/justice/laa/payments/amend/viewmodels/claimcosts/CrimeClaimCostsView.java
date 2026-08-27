package uk.gov.justice.laa.payments.amend.viewmodels.claimcosts;

import static uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsView.putField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.PROFIT_COST;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.VAT;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.TRAVEL_COSTS;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField.WAITING_COSTS;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.payments.amend.models.CrimeClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimCostsView(
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields, boolean hasAssessment)
    implements ClaimCostsView {

  public CrimeClaimCostsView(CrimeClaimDetails claim) {
    this(createCostFields(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createCostFields(
      CrimeClaimDetails claim) {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(costFields, FIXED_FEE, claim);
    putField(costFields, PROFIT_COST, claim);
    putField(costFields, DISBURSEMENTS, claim);
    putField(costFields, TRAVEL_COSTS, claim);
    putField(costFields, WAITING_COSTS, claim);
    putField(costFields, VAT, claim);
    putField(costFields, DISBURSEMENTS_VAT, claim);
    return costFields;
  }
}
