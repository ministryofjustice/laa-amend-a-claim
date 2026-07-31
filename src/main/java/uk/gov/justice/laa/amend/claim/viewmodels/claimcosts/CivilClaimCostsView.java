package uk.gov.justice.laa.amend.claim.viewmodels.claimcosts;

import static uk.gov.justice.laa.amend.claim.viewmodels.claimcosts.ClaimCostsView.putField;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.ADJOURNED_HEARING_FEE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CMRH_ORAL;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.CMRH_TELEPHONE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.COUNSELS_COST;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.DETENTION_TRAVEL;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.HOME_OFFICE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.IS_LONDON_RATE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.JR_FORM_FILLING;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.PRIOR_AUTHORITY_REFERENCE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.SUBSTANTIVE_HEARING;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.FIXED_FEE;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.PROFIT_COST;
import static uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField.VAT;

import java.util.LinkedHashMap;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;

public record CivilClaimCostsView(
    LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> costFields, boolean hasAssessment)
    implements ClaimCostsView {

  public CivilClaimCostsView(CivilClaimDetails claim) {
    this(createCostFields(claim), ClaimCostsView.hasAssessment(claim));
  }

  private static LinkedHashMap<ClaimViewField<?>, ClaimFieldRow> createCostFields(
      CivilClaimDetails claim) {
    var costFields = new LinkedHashMap<ClaimViewField<?>, ClaimFieldRow>();
    putField(costFields, FIXED_FEE, claim);
    putField(costFields, PROFIT_COST, claim);
    putField(costFields, DISBURSEMENTS, claim);
    putField(costFields, COUNSELS_COST, claim);
    putField(costFields, DISBURSEMENTS_VAT, claim);
    putField(costFields, TRAVEL_AND_WAITING_COSTS, claim);
    putField(costFields, VAT, claim);
    putField(costFields, ADJOURNED_HEARING_FEE, claim);
    putField(costFields, DETENTION_TRAVEL, claim);
    putField(costFields, JR_FORM_FILLING, claim);
    putField(costFields, SUBSTANTIVE_HEARING, claim);
    putField(costFields, HOME_OFFICE, claim);
    putField(costFields, CMRH_ORAL, claim);
    putField(costFields, CMRH_TELEPHONE, claim);
    putField(costFields, IS_LONDON_RATE, claim);
    putField(costFields, PRIOR_AUTHORITY_REFERENCE, claim);
    return costFields;
  }
}
