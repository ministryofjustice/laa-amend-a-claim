package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

public class MandatoryFieldRuleConfigTest {

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedCrimeClaimFields")
  void mandatoryRuleConfigCheckForClaimFieldsReturns(ClaimViewField<?> field) {
    mandatoryRuleCheck(field, AreaOfLaw.CRIME_LOWER);
  }

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedMediationClaimFields")
  void mandatoryRuleConfigCheckForMediationClaimFields(ClaimViewField<?> field) {
    mandatoryRuleCheck(field, AreaOfLaw.MEDIATION);
  }

  @ParameterizedTest
  @MethodSource("mandatoryParameterizedCivilClaimFields")
  void mandatoryRuleConfigCheckForCivilClaimFields(ClaimViewField<?> field) {
    mandatoryRuleCheck(field, AreaOfLaw.LEGAL_HELP);
  }

  private static <T extends Claim> void mandatoryRuleCheck(
      ClaimViewField<T> field, AreaOfLaw areaOfLaw) {
    var rules = ClaimFieldRuleJsonLoader.hasRules(field);
    assertThat(rules).isTrue();
    List<FieldRuleSpec> rulesList = ClaimFieldRuleJsonLoader.rulesFor(field);
    assertThat(rulesList).isNotEmpty();
    assertTrue(
        rulesList.stream().anyMatch(spec -> spec.category() == RuleCategory.MANDATORY),
        "Expected at least one mandatory rule");
    assertTrue(
        rulesList.stream().anyMatch(spec -> spec.areasOfLaw().contains(areaOfLaw.name())),
        String.format("Expected at least one rule for %s area of law", areaOfLaw.name()));
  }

  private static Stream<ClaimViewField<?>> mandatoryParameterizedMediationClaimFields() {
    return Stream.of(
        MediationClaimDetailsViewField.OUTREACH_LOCATION,
        MediationClaimDetailsViewField.REFERRAL_SOURCE,
        ClaimDetailsViewField.FORENAME,
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
        ClaimDetailsViewField.CASE_CONCLUDED_DATE,
        CivilClaimDetailsViewField.OUTCOME_FOR_CLIENT,
        CivilClaimDetailsViewField.TRAVEL_AND_WAITING_COSTS,
        ClaimDetailsViewField.FORENAME,
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
