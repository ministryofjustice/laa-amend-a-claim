package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import lombok.Getter;
import uk.gov.justice.laa.amend.claim.constants.AmendClaimConstants.Label;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

@Getter
public enum CostClaimDetailsViewField implements ClaimViewField<ClaimDetails> {
  FIXED_FEE(Label.FIXED_FEE, FieldType.TEXT),
  PROFIT_COST(Label.NET_PROFIT_COST, FieldType.BIG_DECIMAL),
  DISBURSEMENTS(Label.NET_DISBURSEMENTS_COST, FieldType.BIG_DECIMAL),
  DISBURSEMENTS_VAT(Label.DISBURSEMENT_VAT, FieldType.BIG_DECIMAL),
  COUNSELS_COST(Label.COUNSELS_COST, FieldType.BIG_DECIMAL),
  DETENTION_TRAVEL(Label.DETENTION_TRAVEL_COST, FieldType.BIG_DECIMAL),
  JR_FORM_FILLING(Label.JR_FORM_FILLING, FieldType.BIG_DECIMAL),
  TRAVEL(Label.TRAVEL_COSTS, FieldType.BIG_DECIMAL),
  WAITING(Label.WAITING_COSTS, FieldType.BIG_DECIMAL),
  TRAVEL_AND_WAITING_COSTS(Label.TRAVEL_AND_WAITING_COSTS, FieldType.BIG_DECIMAL),
  VAT(Label.VAT, FieldType.BOOLEAN),
  IS_LONDON_RATE(Label.IS_LONDON_RATE, FieldType.BOOLEAN),
  SUBSTANTIVE_HEARING(Label.SUBSTANTIVE_HEARING, FieldType.BOOLEAN),
  ADJOURNED_HEARING_FEE(Label.ADJOURNED_FEE, FieldType.TEXT),
  CMRH_TELEPHONE(Label.CMRH_TELEPHONE, FieldType.TEXT),
  CMRH_ORAL(Label.CMRH_ORAL, FieldType.TEXT),
  HOME_OFFICE(Label.HO_INTERVIEW, FieldType.TEXT),
  PRIOR_AUTHORITY_REFERENCE(Label.PRIOR_AUTHORITY_REFERENCE, FieldType.TEXT);

  private final String key;
  private final FieldType type;

  CostClaimDetailsViewField(String key, FieldType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public String inputKey() {
    return key;
  }

  @Override
  public <U> ClaimViewFieldAccessor<ClaimDetails, U> getAccessor() {
    throw new UnsupportedOperationException(
        "Accessor for cost field '%s' is wired when save is implemented".formatted(name()));
  }

  public static CostClaimDetailsViewField byKey(String key) {
    for (var field : values()) {
      if (field.key.equals(key)) {
        return field;
      }
    }
    throw new IllegalArgumentException("Unknown cost field key: %s".formatted(key));
  }
}
