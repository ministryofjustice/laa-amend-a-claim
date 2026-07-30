package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.VAT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField.WAITING_COSTS;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CrimeClaimCostsView(
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields, boolean hasAssessment)
    implements ClaimCostsView {

  public CrimeClaimCostsView(CrimeClaimDetails claim) {
    this(createCostFields(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createCostFields(
      CrimeClaimDetails claim) {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    ClaimCostsView.putField(costFields, FIXED_FEE, claim);
    ClaimCostsView.putField(costFields, PROFIT_COST, claim);
    ClaimCostsView.putField(costFields, DISBURSEMENTS, claim);
    ClaimCostsView.putField(costFields, TRAVEL_COSTS, claim);
    ClaimCostsView.putField(costFields, WAITING_COSTS, claim);
    ClaimCostsView.putField(costFields, VAT, claim);
    ClaimCostsView.putField(costFields, DISBURSEMENTS_VAT, claim);
    return costFields;
  }
}
