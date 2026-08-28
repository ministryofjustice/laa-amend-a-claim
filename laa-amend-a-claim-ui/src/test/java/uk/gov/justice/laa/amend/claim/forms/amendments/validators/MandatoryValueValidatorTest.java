package uk.gov.justice.laa.amend.claim.forms.amendments.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.model.RuleDto;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;

class MandatoryValueValidatorTest {

  private final MandatoryValueValidator validator = new MandatoryValueValidator();

  @Test
  void rejectsBlankValueWhenRuleAppliesToCrimeArea() {
    var claimDetails = claimDetails(AreaOfLaw.CRIME_LOWER);
    var rule = mandatoryRule(List.of("CRIME"));

    assertThat(validator.isValid(claimDetails, "", rule)).isFalse();
    assertThat(validator.isValid(claimDetails, "   ", rule)).isFalse();
    assertThat(validator.isValid(claimDetails, null, rule)).isFalse();
  }

  @Test
  void rejectsBlankValueWhenRuleAppliesToCivilArea() {
    var claimDetails = claimDetails(AreaOfLaw.LEGAL_HELP);
    var rule = mandatoryRule(List.of("CIVIL"));

    assertThat(validator.isValid(claimDetails, "", rule)).isFalse();
    assertThat(validator.isValid(claimDetails, "value", rule)).isTrue();
  }

  @Test
  void rejectsBlankValueWhenRuleAppliesToMediationArea() {
    var claimDetails = claimDetails(AreaOfLaw.MEDIATION);
    var rule = mandatoryRule(List.of("MEDIATION"));

    assertThat(validator.isValid(claimDetails, " ", rule)).isFalse();
    assertThat(validator.isValid(claimDetails, "value", rule)).isTrue();
  }

  @Test
  void rejectsBlankValueWhenRuleAppliesToMoreThanOneAreaOfLaw() {
    var crimeClaimDetails = claimDetails(AreaOfLaw.CRIME_LOWER);
    var civilClaimDetails = claimDetails(AreaOfLaw.LEGAL_HELP);
    var rule = mandatoryRule(List.of("CRIME", "CIVIL"));

    assertThat(validator.isValid(crimeClaimDetails, "", rule)).isFalse();
    assertThat(validator.isValid(civilClaimDetails, "", rule)).isFalse();
  }

  @Test
  void acceptsBlankValueWhenRuleDoesNotApplyToAreaOfLaw() {
    var claimDetails = claimDetails(AreaOfLaw.MEDIATION);
    var rule = mandatoryRule(List.of("CRIME", "CIVIL"));

    assertThat(validator.isValid(claimDetails, "", rule)).isTrue();
    assertThat(validator.isValid(claimDetails, null, rule)).isTrue();
  }

  private static RuleDto mandatoryRule(List<String> areasOfLaw) {
    return new RuleDto(
        "MANDATORY",
        "mandatory",
        "amendmentForm.mandatoryField.error",
        areasOfLaw,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  private static ClaimDetails claimDetails(AreaOfLaw areaOfLaw) {
    var claimDetails = mock(ClaimDetails.class);
    when(claimDetails.getAreaOfLaw()).thenReturn(areaOfLaw);
    return claimDetails;
  }
}
