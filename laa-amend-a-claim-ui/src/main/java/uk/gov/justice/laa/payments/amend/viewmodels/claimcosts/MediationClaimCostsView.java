package uk.gov.justice.laa.payments.amend.viewmodels.claimcosts;

import static uk.gov.justice.laa.payments.amend.viewmodels.claimcosts.ClaimCostsView.putField;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField.VAT;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.payments.amend.models.MediationClaimDetails;
import uk.gov.justice.laa.payments.amend.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;

public record MediationClaimCostsView(
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields, boolean hasAssessment)
    implements ClaimCostsView {

  public MediationClaimCostsView(MediationClaimDetails claim) {
    this(createCostFields(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createCostFields(
      MediationClaimDetails claim) {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(costFields, FIXED_FEE, claim);
    putField(costFields, VAT, claim);
    putField(costFields, DISBURSEMENTS, claim);
    putField(costFields, DISBURSEMENTS_VAT, claim);
    return costFields;
  }
}
