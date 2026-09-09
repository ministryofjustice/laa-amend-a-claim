package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.payments.amend.models.enums.AreaOfLaw;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

public class MandatoryFieldRuleConfigTest {

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedCrimeClaimFields")
  void fieldShouldHaveMandatoryRuleForCrimeAreaOfLaw(ClaimViewField<?> field) {
    assertMandatoryRuleExists(field, AreaOfLaw.CRIME_LOWER);
  }

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedMediationClaimFields")
  void fieldShouldHaveMandatoryRuleForMediationAreaOfLaw(ClaimViewField<?> field) {
    assertMandatoryRuleExists(field, AreaOfLaw.MEDIATION);
  }

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedCivilClaimFields")
  void fieldShouldHaveMandatoryRuleForLegalHelpAreaOfLaw(ClaimViewField<?> field) {
    assertMandatoryRuleExists(field, AreaOfLaw.LEGAL_HELP);
  }

  private static void assertMandatoryRuleExists(ClaimViewField<?> field, AreaOfLaw areaOfLaw) {
    assertThat(ClaimFieldRuleJsonLoader.hasRules(field)).isTrue();

    var rulesList = ClaimFieldRuleJsonLoader.rulesFor(field);

    assertThat(rulesList)
        .as("Field %s should have a mandatory rule covering %s", field.name(), areaOfLaw.name())
        .anySatisfy(
            rule -> {
              assertThat(rule.category())
                  .as("Rule category for field %s", field.name())
                  .isEqualTo(RuleCategory.MANDATORY);

              assertThat(rule.areasOfLaw())
                  .as("Areas of law for field %s", field.name())
                  .contains(areaOfLaw.name());
            });
  }

  private static Stream<ClaimViewField<?>> mandatoryParameterizedMediationClaimFields() {
    return Stream.of(
        MediationClaimDetailsViewField.OUTREACH_LOCATION,
        MediationClaimDetailsViewField.REFERRAL_SOURCE,
        MediationClaimDetailsViewField.FORENAME,
        MediationClaimDetailsViewField.CLIENT_2_FORENAME,
        ClaimDetailsViewField.SURNAME,
        MediationClaimDetailsViewField.CLIENT_2_SURNAME,
        MediationClaimDetailsViewField.DATE_OF_BIRTH,
        MediationClaimDetailsViewField.CLIENT_2_DATE_OF_BIRTH,
        MediationClaimDetailsViewField.UNIQUE_CLIENT_NUMBER,
        MediationClaimDetailsViewField.POSTCODE,
        MediationClaimDetailsViewField.CLIENT_2_POSTCODE,
        ClaimDetailsViewField.GENDER,
        ClaimDetailsViewField.ETHNICITY,
        ClaimDetailsViewField.DISABILITY,
        MediationClaimDetailsViewField.IS_LEGALLY_AIDED,
        MediationClaimDetailsViewField.IS_CLIENT_2_LEGALLY_AIDED,
        MediationClaimDetailsViewField.CLAIM_ID,
        ClaimDetailsViewField.CASE_START_DATE,
        ClaimDetailsViewField.CASE_REFERENCE_NUMBER,
        MediationClaimDetailsViewField.SCHEDULE_REFERENCE,
        MediationClaimDetailsViewField.MATTER_TYPE_CODE_1,
        MediationClaimDetailsViewField.MATTER_TYPE_CODE_2,
        MediationClaimDetailsViewField.UNIQUE_CASE_ID);
  }

  private static Stream<ClaimViewField<?>> mandatoryParameterizedCivilClaimFields() {
    return Stream.of(
        CivilClaimDetailsViewField.UNIQUE_FILE_NUMBER,
        ClaimDetailsViewField.CASE_START_DATE,
        CivilClaimDetailsViewField.CASE_CONCLUDED_CLAIMED_DATE,
        CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT,
        CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS,
        CivilClaimDetailsViewField.FORENAME,
        ClaimDetailsViewField.SURNAME,
        CivilClaimDetailsViewField.DATE_OF_BIRTH,
        CivilClaimDetailsViewField.UNIQUE_CLIENT_NUMBER,
        CivilClaimDetailsViewField.POSTCODE,
        ClaimDetailsViewField.GENDER,
        ClaimDetailsViewField.ETHNICITY,
        ClaimDetailsViewField.DISABILITY,
        CivilClaimDetailsViewField.ADVICE_TIME,
        CivilClaimDetailsViewField.TRAVEL_TIME,
        CivilClaimDetailsViewField.WAITING_TIME,
        CivilClaimDetailsViewField.COUNSELS_COST,
        CivilClaimDetailsViewField.CASE_ID,
        ClaimDetailsViewField.CASE_REFERENCE_NUMBER,
        CivilClaimDetailsViewField.SCHEDULE_REFERENCE_CIVIL,
        CivilClaimDetailsViewField.MATTER_TYPE_CODE_1,
        CivilClaimDetailsViewField.MATTER_TYPE_CODE_2,
        ClaimDetailsViewField.PROFIT_COST,
        ClaimDetailsViewField.VAT);
  }

  private static Stream<ClaimViewField<?>> mandatoryParameterizedCrimeClaimFields() {
    return Stream.of(
        CrimeClaimDetailsViewField.CASE_CONCLUDED_DATE,
        CrimeClaimDetailsViewField.STAGE_REACHED,
        ClaimDetailsViewField.PROFIT_COST,
        ClaimDetailsViewField.DISBURSEMENTS_VAT);
  }
}
