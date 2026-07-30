package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.VAT;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record MediationClaimCostsView(
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields, boolean hasAssessment)
    implements ClaimCostsView {

  public MediationClaimCostsView(MediationClaimDetails claim) {
    this(createCostFields(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createCostFields(
      MediationClaimDetails claim) {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    ClaimCostsView.putField(costFields, FIXED_FEE, claim);
    ClaimCostsView.putField(costFields, VAT, claim);
    ClaimCostsView.putField(costFields, DISBURSEMENTS, claim);
    ClaimCostsView.putField(costFields, DISBURSEMENTS_VAT, claim);
    return costFields;
  }
}
