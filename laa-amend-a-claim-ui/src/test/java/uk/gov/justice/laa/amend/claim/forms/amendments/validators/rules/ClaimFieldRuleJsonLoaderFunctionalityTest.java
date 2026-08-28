package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules.model.RuleDto;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.enums.AreaOfLaw;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;

public class ClaimFieldRuleJsonLoaderFunctionalityTest {

  @Test
  void ruleGroupsForFieldAreInJsonDeclarationOrder() {
    var testJson = createTestJsonWithOrderedRuleGroups();
    var rules = ClaimFieldRuleJsonLoader.loadFromContent(testJson);
    var foreNameRules = rules.get(ClaimDetailsViewField.FORENAME);

    assertThat(foreNameRules).hasSize(3);
    assertThat(foreNameRules.get(0).category()).isEqualTo(RuleCategory.MANDATORY);
    assertThat(foreNameRules.get(1).category()).isEqualTo(RuleCategory.FORMAT);
    assertThat(foreNameRules.get(2).category()).isEqualTo(RuleCategory.LENGTH);
  }

  // Tests for toPredicate method
  @Test
  void toPredicateRegexKindReturnsInvalidWhenPatternDoesNotMatch() {
    var rule =
        new RuleDto(
            "VALIDATION", "regex", "error.code", null, null, "^[a-z]+$", null, null, null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "abc")).isFalse();
    assertThat(predicate.test(null, "ABC")).isTrue();
    assertThat(predicate.test(null, "123")).isTrue();
  }

  @Test
  void toPredicateRegexKindWithCaseInsensitiveFlag() {
    var rule =
        new RuleDto(
            "VALIDATION",
            "regex",
            "error.code",
            null,
            null,
            "^[a-z]+$",
            List.of("CASE_INSENSITIVE"),
            null,
            null,
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "abc")).isFalse();
    assertThat(predicate.test(null, "ABC")).isFalse();
    assertThat(predicate.test(null, "123")).isTrue();
  }

  @Test
  void toPredicateMaxLengthKindReturnsInvalidWhenValueExceedsMax() {
    var rule =
        new RuleDto(
            "VALIDATION", "maxLength", "error.code", null, null, null, null, null, "5", null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "abc")).isFalse();
    assertThat(predicate.test(null, "abcde")).isFalse();
    assertThat(predicate.test(null, "abcdef")).isTrue();
  }

  @Test
  void toPredicateMinLengthKindReturnsInvalidWhenValueBelowMin() {
    var rule =
        new RuleDto(
            "VALIDATION", "minLength", "error.code", null, null, null, null, "3", null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "ab")).isTrue();
    assertThat(predicate.test(null, "abc")).isFalse();
    assertThat(predicate.test(null, "abcd")).isFalse();
  }

  @Test
  void toPredicateExactLengthKindReturnsInvalidWhenValueLengthDoesNotMatch() {
    var rule =
        new RuleDto(
            "VALIDATION", "exactLength", "error.code", null, null, null, null, null, null, 5);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "abc")).isTrue();
    assertThat(predicate.test(null, "abcde")).isFalse();
    assertThat(predicate.test(null, "abcdef")).isTrue();
  }

  @Test
  void toPredicateIntRangeKindReturnsInvalidWhenValueOutOfRange() {
    var rule =
        new RuleDto(
            "VALIDATION", "intRange", "error.code", null, null, null, null, "10", "20", null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "9")).isTrue();
    assertThat(predicate.test(null, "10")).isFalse();
    assertThat(predicate.test(null, "15")).isFalse();
    assertThat(predicate.test(null, "20")).isFalse();
    assertThat(predicate.test(null, "21")).isTrue();
  }

  @Test
  void toPredicateIntRangeKindIgnoresInvalidIntegers() {
    var rule =
        new RuleDto(
            "VALIDATION", "intRange", "error.code", null, null, null, null, "10", "20", null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "not-a-number")).isFalse();
    assertThat(predicate.test(null, "12.34")).isFalse();
  }

  @Test
  void toPredicateIntRangeKindTrimsWhitespace() {
    var rule =
        new RuleDto(
            "VALIDATION", "intRange", "error.code", null, null, null, null, "10", "20", null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "  15  ")).isFalse();
    assertThat(predicate.test(null, "  5  ")).isTrue();
  }

  @Test
  void toPredicateDecimalRangeKindReturnsInvalidWhenValueOutOfRange() {
    var rule =
        new RuleDto(
            "VALIDATION",
            "decimalRange",
            "error.code",
            null,
            null,
            null,
            null,
            "10.5",
            "20.5",
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "10.4")).isTrue();
    assertThat(predicate.test(null, "10.5")).isFalse();
    assertThat(predicate.test(null, "15.5")).isFalse();
    assertThat(predicate.test(null, "20.5")).isFalse();
    assertThat(predicate.test(null, "20.6")).isTrue();
  }

  @Test
  void toPredicateDecimalRangeKindIgnoresInvalidDecimals() {
    var rule =
        new RuleDto(
            "VALIDATION",
            "decimalRange",
            "error.code",
            null,
            null,
            null,
            null,
            "10.5",
            "20.5",
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "not-a-number")).isFalse();
  }

  @Test
  void toPredicateDecimalRangeKindTrimsWhitespace() {
    var rule =
        new RuleDto(
            "VALIDATION",
            "decimalRange",
            "error.code",
            null,
            null,
            null,
            null,
            "10.5",
            "20.5",
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "  15.5  ")).isFalse();
    assertThat(predicate.test(null, "  9.5  ")).isTrue();
  }

  @Test
  void toPredicateDecimalMinKindReturnsInvalidWhenValueBelowMin() {
    var rule =
        new RuleDto(
            "VALIDATION", "decimalMin", "error.code", null, null, null, null, "10.5", null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "10.4")).isTrue();
    assertThat(predicate.test(null, "10.5")).isFalse();
    assertThat(predicate.test(null, "15.5")).isFalse();
  }

  @Test
  void toPredicateDecimalMinKindIgnoresInvalidDecimals() {
    var rule =
        new RuleDto(
            "VALIDATION", "decimalMin", "error.code", null, null, null, null, "10.5", null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "not-a-number")).isFalse();
  }

  @Test
  void toPredicateDecimalMinKindTrimsWhitespace() {
    var rule =
        new RuleDto(
            "VALIDATION", "decimalMin", "error.code", null, null, null, null, "10.5", null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(null, "  10.5  ")).isFalse();
    assertThat(predicate.test(null, "  9.5  ")).isTrue();
  }

  @Test
  void toPredicateMandatoryKindDelegatesValidation() {
    var rule =
        new RuleDto(
            "VALIDATION", "mandatory", "error.code", null, null, null, null, null, null, null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);
    assertThat(predicate).isNotNull();
  }

  @Test
  void toPredicateMandatoryKindReturnsInvalidWhenValueIsBlankForApplicableAreaOfLaw() {
    var claimDetails = Mockito.mock(ClaimDetails.class);
    Mockito.when(claimDetails.getAreaOfLaw()).thenReturn(AreaOfLaw.CRIME_LOWER);

    var rule =
        new RuleDto(
            "VALIDATION",
            "mandatory",
            "error.code",
            List.of("CRIME_LOWER", "LEGAL_HELP"),
            null,
            null,
            null,
            null,
            null,
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(claimDetails, "")).isTrue();
    assertThat(predicate.test(claimDetails, "   ")).isTrue();
    assertThat(predicate.test(claimDetails, null)).isTrue();
  }

  @Test
  void toPredicateMandatoryKindReturnsValidWhenValueIsNotBlankForApplicableAreaOfLaw() {
    var claimDetails = Mockito.mock(ClaimDetails.class);
    Mockito.when(claimDetails.getAreaOfLaw()).thenReturn(AreaOfLaw.CRIME_LOWER);

    var rule =
        new RuleDto(
            "VALIDATION",
            "mandatory",
            "error.code",
            List.of("CRIME_LOWER", "LEGAL_HELP"),
            null,
            null,
            null,
            null,
            null,
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(claimDetails, "value")).isFalse();
    assertThat(predicate.test(claimDetails, "  value  ")).isFalse();
  }

  @Test
  void toPredicateMandatoryKindReturnsValidWhenAreaOfLawNotInRule() {
    var claimDetails = Mockito.mock(ClaimDetails.class);
    Mockito.when(claimDetails.getAreaOfLaw()).thenReturn(AreaOfLaw.MEDIATION);

    var rule =
        new RuleDto(
            "VALIDATION",
            "mandatory",
            "error.code",
            List.of("CRIME_LOWER", "LEGAL_HELP"),
            null,
            null,
            null,
            null,
            null,
            null);

    var predicate = ClaimFieldRuleJsonLoader.toPredicate(rule);

    assertThat(predicate.test(claimDetails, "")).isFalse();
    assertThat(predicate.test(claimDetails, null)).isFalse();
    assertThat(predicate.test(claimDetails, "any value")).isFalse();
  }

  @Test
  void toPredicateUnknownKindThrowsIllegalStateException() {
    var rule =
        new RuleDto(
            "VALIDATION", "unknownKind", "error.code", null, null, null, null, null, null, null);

    assertThatThrownBy(() -> ClaimFieldRuleJsonLoader.toPredicate(rule))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("unknown rule kind");
  }

  private static String createTestJsonWithOrderedRuleGroups() {
    return """
        {
          "ruleGroups": {
            "groupFirst": [
              {"category": "MANDATORY", "kind": "mandatory", "messageCode": "msg1"}
            ],
            "groupSecond": [
              {"category": "FORMAT", "kind": "regex", "pattern": ".*", "messageCode": "msg2"}
            ],
            "groupThird": [
              {"category": "LENGTH", "kind": "maxLength", "max": "10", "messageCode": "msg3"}
            ]
          },
          "fields": {
            "ClaimDetailsViewField.FORENAME": {
              "ruleGroups": ["groupFirst", "groupSecond", "groupThird"]
            }
          }
        }
        """;
  }
}
