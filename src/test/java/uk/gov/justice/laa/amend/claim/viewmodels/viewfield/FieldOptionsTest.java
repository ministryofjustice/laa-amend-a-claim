package uk.gov.justice.laa.amend.claim.viewmodels.viewfield;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FieldOptionsTest {

  @Test
  void exposesEnumValuesAndMessageKeysAsFieldOptions() {
    assertThat(FieldOptions.REFERRAL_SOURCE)
        .containsExactly(
            ReferralSource.REFERRAL_FROM_SOLICITOR,
            ReferralSource.REFERRAL_FROM_COURT,
            ReferralSource.REFERRAL_FROM_CAB,
            ReferralSource.REFERRAL_FROM_OTHER_ADVICE_AGENCY_OR_TELEPHONE_HELPLINE,
            ReferralSource.REFERRAL_FROM_RELATE_OR_OTHER_RELATIONSHIP_COUNSELLING,
            ReferralSource.REFERRAL_FROM_GP_NHS,
            ReferralSource.CLIENT_SELF_REFERRED,
            ReferralSource.OTHER,
            ReferralSource.UNKNOWN,
            ReferralSource.SPIP);

    assertThat(ReferralSource.CLIENT_SELF_REFERRED.value()).isEqualTo("08");
    assertThat(ReferralSource.CLIENT_SELF_REFERRED.messageKey())
        .isEqualTo("claimCase.options.referralSource.CLIENT_SELF_REFERRED");
  }

  @Test
  void outcomeCodesWithDuplicateDescriptionsHaveUniqueMessageKeys() {
    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR.value()).isEqualTo("CN02");
    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR.messageKey())
        .isEqualTo("claimCase.options.outcomeCode.CHANGE_OF_SOLICITOR");

    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR_CP02.value()).isEqualTo("CP02");
    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR_CP02.messageKey())
        .isEqualTo("claimCase.options.outcomeCode.CHANGE_OF_SOLICITOR_CP02");

    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR_PL02.value()).isEqualTo("PL02");
    assertThat(OutcomeCode.CHANGE_OF_SOLICITOR_PL02.messageKey())
        .isEqualTo("claimCase.options.outcomeCode.CHANGE_OF_SOLICITOR_PL02");
  }
}
