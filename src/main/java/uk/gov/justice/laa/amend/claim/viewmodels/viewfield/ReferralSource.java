package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

public enum ReferralSource implements FieldOption {
  REFERRAL_FROM_SOLICITOR("02"),
  REFERRAL_FROM_COURT("03"),
  REFERRAL_FROM_CAB("04"),
  REFERRAL_FROM_OTHER_ADVICE_AGENCY_OR_TELEPHONE_HELPLINE("05"),
  REFERRAL_FROM_RELATE_OR_OTHER_RELATIONSHIP_COUNSELLING("06"),
  REFERRAL_FROM_GP_NHS("07"),
  CLIENT_SELF_REFERRED("08"),
  OTHER("09"),
  UNKNOWN("10"),
  SPIP("11");

  private final String value;

  ReferralSource(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
