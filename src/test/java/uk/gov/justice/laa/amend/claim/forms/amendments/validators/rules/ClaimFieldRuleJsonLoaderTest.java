package uk.gov.justice.laa.amend.claim.forms.amendments.validators.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import uk.gov.justice.laa.amend.claim.support.TestMessageSources;

class ClaimFieldRuleJsonLoaderTest {

  private final JsonNode root = readResource(ClaimFieldRuleJsonLoader.RESOURCE_PATH);

  @Test
  void loadsWithoutThrowing() {
    assertThatCode(() -> ClaimFieldRuleJsonLoader.load(ClaimFieldRuleJsonLoader.RESOURCE_PATH))
        .doesNotThrowAnyException();
  }

  @Test
  void everyFieldNameResolvesToClaimViewFieldConstant() {
    for (var fieldName : fieldNames()) {
      assertThatCode(() -> ClaimFieldRuleJsonLoader.resolveField(fieldName))
          .as("field name '%s' should resolve to a real ClaimViewField constant", fieldName)
          .doesNotThrowAnyException();
    }
  }

  @Test
  void everyRuleGroupReferenceExists() {
    var ruleGroupNames = new HashSet<>(root.path("ruleGroups").propertyNames());

    for (var entry : root.path("fields").properties()) {
      var fieldNode = entry.getValue();
      if (fieldNode.has("ruleGroup")) {
        var groupName = fieldNode.get("ruleGroup").asString();
        assertThat(ruleGroupNames)
            .as("field '%s' references ruleGroup '%s'", entry.getKey(), groupName)
            .contains(groupName);
      }
    }
  }

  @Test
  void everyRegexPatternCompiles() {
    for (var groupEntry : root.path("ruleGroups").properties()) {
      for (JsonNode ruleNode : groupEntry.getValue()) {
        if ("regex".equals(ruleNode.path("kind").asString())) {
          var pattern = ruleNode.get("pattern").asString();
          assertThatCode(() -> Pattern.compile(pattern))
              .as(
                  "regex pattern '%s' in ruleGroup '%s' should compile",
                  pattern, groupEntry.getKey())
              .doesNotThrowAnyException();
        }
      }
    }
  }

  @Test
  void everyMessageCodeHasCorrespondingMessagesPropertiesEntry() {
    var messageSource = TestMessageSources.real();
    for (var groupEntry : root.path("ruleGroups").properties()) {
      for (JsonNode ruleNode : groupEntry.getValue()) {
        var messageCode = ruleNode.get("messageCode").asString();
        assertThatCode(() -> messageSource.getMessage(messageCode, null, java.util.Locale.UK))
            .as(
                "messageCode '%s' in ruleGroup '%s' should exist in messages.properties",
                messageCode, groupEntry.getKey())
            .doesNotThrowAnyException();
      }
    }
  }

  @Test
  void anUnknownFieldNameThrowsAtLoad() {
    assertThatThrownBy(() -> ClaimFieldRuleJsonLoader.resolveField("NotARealEnum.SOMETHING"))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void anInvalidRegexThrowsWhenCompiled() {
    assertThatThrownBy(() -> Pattern.compile("[")).isInstanceOf(PatternSyntaxException.class);
  }

  private Set<String> fieldNames() {
    return new HashSet<>(root.path("fields").propertyNames());
  }

  private static JsonNode readResource(String resourcePath) {
    try (InputStream in = ClaimFieldRuleJsonLoaderTest.class.getResourceAsStream(resourcePath)) {
      return new ObjectMapper().readTree(in);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
