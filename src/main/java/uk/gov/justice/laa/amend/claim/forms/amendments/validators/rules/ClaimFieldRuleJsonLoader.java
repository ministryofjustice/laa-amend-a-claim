package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CivilClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.ClaimViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.CrimeClaimDetailsViewField;
import uk.gov.justice.laa.amend.claim.viewmodels.viewfield.MediationClaimDetailsViewField;

/**
 * Parses {@code claim-field-rules.json} once at class-init into the {@code Map<ClaimViewField<?>,
 * List<FieldRuleSpec>>}.
 */
public final class ClaimFieldRuleJsonLoader {

  static final String RESOURCE_PATH = "/claim-field-rules.json";

  private static final Map<ClaimViewField<?>, List<FieldRuleSpec>> RULES = load(RESOURCE_PATH);

  private ClaimFieldRuleJsonLoader() {}

  public static boolean hasRules(ClaimViewField<?> field) {
    return RULES.containsKey(field);
  }

  public static List<FieldRuleSpec> rulesFor(ClaimViewField<?> field) {
    return RULES.getOrDefault(field, List.of());
  }

  static Map<ClaimViewField<?>, List<FieldRuleSpec>> load(String resourcePath) {
    var root = readTree(resourcePath);

    Map<String, List<FieldRuleSpec>> ruleGroups = new HashMap<>();
    for (var entry : root.path("ruleGroups").properties()) {
      ruleGroups.put(entry.getKey(), toRuleSpecs(entry.getValue()));
    }

    Map<ClaimViewField<?>, List<FieldRuleSpec>> rules = new HashMap<>();
    for (var entry : root.path("fields").properties()) {
      var fieldName = entry.getKey();
      var field = resolveField(fieldName);
      var fieldNode = entry.getValue();

      List<FieldRuleSpec> ruleSpecs;
      if (fieldNode.has("ruleGroup")) {
        var groupName = fieldNode.get("ruleGroup").asString();
        ruleSpecs = ruleGroups.get(groupName);
        if (ruleSpecs == null) {
          throw new IllegalStateException(
              "claim-field-rules.json: field '%s' references unknown ruleGroup '%s'"
                  .formatted(fieldName, groupName));
        }
      } else {
        ruleSpecs = toRuleSpecs(fieldNode.path("rules"));
      }

      rules.put(field, ruleSpecs);
    }

    return Map.copyOf(rules);
  }

  private static JsonNode readTree(String resourcePath) {
    try (InputStream in = ClaimFieldRuleJsonLoader.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalStateException(
            "claim-field-rules.json not found on classpath at %s".formatted(resourcePath));
      }
      return new ObjectMapper().readTree(in);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to parse claim-field-rules.json", e);
    }
  }

  private static List<FieldRuleSpec> toRuleSpecs(JsonNode rulesNode) {
    List<FieldRuleSpec> specs = new ArrayList<>();
    for (JsonNode ruleNode : rulesNode) {
      specs.add(toRuleSpec(ruleNode));
    }
    return List.copyOf(specs);
  }

  private static FieldRuleSpec toRuleSpec(JsonNode ruleNode) {
    var category = RuleCategory.valueOf(ruleNode.get("category").asString());
    var messageCode = ruleNode.get("messageCode").asString();
    var messageArgs = toMessageArgs(ruleNode.path("messageArgs"));
    var isInvalid = toPredicate(ruleNode);
    return new FieldRuleSpec(category, messageCode, isInvalid, messageArgs);
  }

  private static List<Object> toMessageArgs(JsonNode messageArgsNode) {
    List<Object> args = new ArrayList<>();
    for (JsonNode arg : messageArgsNode) {
      args.add(arg.asString());
    }
    return List.copyOf(args);
  }

  private static Predicate<String> toPredicate(JsonNode ruleNode) {
    var kind = ruleNode.get("kind").asString();
    return switch (kind) {
      case "regex" -> {
        var pattern = compilePattern(ruleNode);
        yield value -> !pattern.matcher(value).matches();
      }
      case "maxLength" -> {
        var max = ruleNode.get("max").asInt();
        yield value -> value.length() > max;
      }
      case "minLength" -> {
        var min = ruleNode.get("min").asInt();
        yield value -> value.length() < min;
      }
      case "exactLength" -> {
        var length = ruleNode.get("length").asInt();
        yield value -> value.length() != length;
      }
      case "intRange" -> {
        var min = ruleNode.get("min").asInt();
        var max = ruleNode.get("max").asInt();
        yield value -> isIntegerOutOfRange(value, min, max);
      }
      case "decimalRange" -> {
        var min = new BigDecimal(ruleNode.get("min").asString());
        var max = new BigDecimal(ruleNode.get("max").asString());
        yield value -> isDecimalOutOfRange(value, min, max);
      }
      case "decimalMin" -> {
        var min = new BigDecimal(ruleNode.get("min").asString());
        yield value -> isDecimalBelowMin(value, min);
      }
      default ->
          throw new IllegalStateException(
              "claim-field-rules.json: unknown rule kind '%s'".formatted(kind));
    };
  }

  private static Pattern compilePattern(JsonNode ruleNode) {
    var pattern = ruleNode.get("pattern").asString();
    int flags = 0;
    for (JsonNode flagNode : ruleNode.path("flags")) {
      flags |= toPatternFlag(flagNode.asString());
    }
    return Pattern.compile(pattern, flags);
  }

  private static int toPatternFlag(String flagName) {
    if ("CASE_INSENSITIVE".equals(flagName)) {
      return Pattern.CASE_INSENSITIVE;
    }
    throw new IllegalStateException(
        "claim-field-rules.json: unknown regex flag '%s'".formatted(flagName));
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
          "claim-field-rules.json: field name '%s' must be of the form EnumClass.CONSTANT"
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
              "claim-field-rules.json: unknown ClaimViewField enum '%s' for field '%s'"
                  .formatted(enumClassName, qualifiedName));
    };
  }
}
