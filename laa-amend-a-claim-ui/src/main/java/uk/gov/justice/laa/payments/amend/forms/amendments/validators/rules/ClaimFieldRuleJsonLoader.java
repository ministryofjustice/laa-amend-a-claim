package uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import tools.jackson.databind.ObjectMapper;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules.model.RuleDto;
import uk.gov.justice.laa.payments.amend.forms.amendments.validators.rules.model.RulesRoot;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.payments.amend.viewmodels.viewfield.MediationClaimDetailsViewField;

/**
 * Parses {@code /validation/amendments-claim-field-rules.json} once at class-init into the {@code
 * Map<ClaimViewField<?>, List<FieldRuleSpec>>}.
 */
public final class ClaimFieldRuleJsonLoader {

  static final String RESOURCE_PATH = "/validation/amendments-claim-field-rules.json";

  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES = load(RESOURCE_PATH);

  private ClaimFieldRuleJsonLoader() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }

  static Map<ClaimViewField<?>, List<FieldRuleSpec>> load(String resourcePath) {
    // Deserialize JSON rules from the specified resource path
    var rulesRoot = readValue(resourcePath);

    // Loops through all rule groups to fetch each field's rule specifications
    Map<String, List<FieldRuleSpec>> ruleGroups = new HashMap<>();
    rulesRoot
        .ruleGroups()
        .forEach((groupName, ruleDtos) -> ruleGroups.put(groupName, toRuleSpecs(ruleDtos)));

    // Loops through each rule group to fetch each field's ClaimViewField object
    Map<ClaimViewField<?>, List<FieldRuleSpec>> result = new HashMap<>();
    rulesRoot
        .fields()
        .forEach(
            (fieldName, fieldConfig) -> {
              var field = resolveField(fieldName);
              List<FieldRuleSpec> ruleSpecs = ruleGroups.get(fieldConfig.ruleGroup());

              if (ruleSpecs == null) {
                throw new IllegalStateException(
                    "amendments-claim-field-rules.json: field '%s' references unknown ruleGroup '%s'"
                        .formatted(fieldName, fieldConfig.ruleGroup()));
              }

              result.put(field, ruleSpecs);
            });

    return result;
  }

  private static RulesRoot readValue(String resourcePath) {
    try (InputStream in = ClaimFieldRuleJsonLoader.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalStateException(
            "amendments-claim-field-rules.json not found on classpath at %s"
                .formatted(resourcePath));
      }
      return new ObjectMapper().readValue(in, RulesRoot.class);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to parse amendments-claim-field-rules.json", e);
    }
  }

  private static List<FieldRuleSpec> toRuleSpecs(List<RuleDto> ruleDtos) {
    return ruleDtos.stream().map(ClaimFieldRuleJsonLoader::toRuleSpec).toList();
  }

  private static FieldRuleSpec toRuleSpec(RuleDto rule) {
    var category = RuleCategory.valueOf(rule.category());
    List<Object> messageArgs =
        rule.messageArgs() != null
            ? rule.messageArgs().stream().<Object>map(s -> s).toList()
            : List.of();
    return new FieldRuleSpec(category, rule.messageCode(), toPredicate(rule), messageArgs);
  }

  private static Predicate<String> toPredicate(RuleDto rule) {
    return switch (rule.kind()) {
      case "regex" -> {
        var pattern = compilePattern(rule);
        yield value -> !pattern.matcher(value).matches();
      }
      case "maxLength" -> {
        var max = Integer.parseInt(rule.max());
        yield value -> value.length() > max;
      }
      case "minLength" -> {
        var min = Integer.parseInt(rule.min());
        yield value -> value.length() < min;
      }
      case "exactLength" -> {
        var length = rule.length();
        yield value -> value.length() != length;
      }
      case "intRange" -> {
        var min = Integer.parseInt(rule.min());
        var max = Integer.parseInt(rule.max());
        yield value -> isIntegerOutOfRange(value, min, max);
      }
      case "decimalRange" -> {
        var min = new BigDecimal(rule.min());
        var max = new BigDecimal(rule.max());
        yield value -> isDecimalOutOfRange(value, min, max);
      }
      case "decimalMin" -> {
        var min = new BigDecimal(rule.min());
        yield value -> isDecimalBelowMin(value, min);
      }
      default ->
          throw new IllegalStateException(
              "amendments-claim-field-rules.json: unknown rule kind '%s'".formatted(rule.kind()));
    };
  }

  private static Pattern compilePattern(RuleDto rule) {
    int flags = 0;
    // Currently properties could only have a maximum of one flag, but the JSON is setup to
    // support multiple flags in the future. This loop ensures that multiple flags are added
    // together then added as a flag for the Pattern.compile command.
    if (rule.flags() != null) {
      for (String flagName : rule.flags()) {
        flags |= toPatternFlag(flagName);
      }
    }
    // This uses java.util.regex.Pattern to add a flag to ignore specific rules when compiling
    // the pattern. The only flag currently supported is CASE_INSENSITIVE.
    return Pattern.compile(rule.pattern(), flags);
  }

  private static int toPatternFlag(String flagName) {
    if ("CASE_INSENSITIVE".equals(flagName)) {
      return Pattern.CASE_INSENSITIVE;
    }
    throw new IllegalStateException(
        "amendments-claim-field-rules.json: unknown regex flag '%s'".formatted(flagName));
  }

  private static boolean isIntegerOutOfRange(String value, int min, int max) {
    try {
      var parsed = Integer.parseInt(value.trim());
      return parsed < min || parsed > max;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean isDecimalOutOfRange(String value, BigDecimal min, BigDecimal max) {
    try {
      var parsed = new BigDecimal(value.trim());
      return parsed.compareTo(min) < 0 || parsed.compareTo(max) > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static boolean isDecimalBelowMin(String value, BigDecimal min) {
    try {
      var parsed = new BigDecimal(value.trim());
      return parsed.compareTo(min) < 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  static ClaimViewField<?> resolveField(String qualifiedName) {
    var separatorIndex = qualifiedName.indexOf('.');
    if (separatorIndex < 0) {
      throw new IllegalStateException(
          "amendments-claim-field-rules.json: field name '%s' must be of the form EnumClass.CONSTANT"
              .formatted(qualifiedName));
    }
    var enumClassName = qualifiedName.substring(0, separatorIndex);
    var constantName = qualifiedName.substring(separatorIndex + 1);

    return switch (enumClassName) {
      case "ClaimDetailsViewField" -> ClaimDetailsViewField.valueOf(constantName);
      case "CivilClaimDetailsViewField" -> CivilClaimDetailsViewField.valueOf(constantName);
      case "CrimeClaimDetailsViewField" -> CrimeClaimDetailsViewField.valueOf(constantName);
      case "MediationClaimDetailsViewField" -> MediationClaimDetailsViewField.valueOf(constantName);
      default ->
          throw new IllegalStateException(
              "amendments-claim-field-rules.json: unknown ClaimViewField enum '%s' for field '%s'"
                  .formatted(enumClassName, qualifiedName));
    };
  }
}
