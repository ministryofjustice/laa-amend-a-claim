package uk.gov.justice.laa.amend.claim.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.tests.ClaimDetailsTest;

public class E2ETestHelper {

  public static ClaimDetailsFixture loadFixture(String resourcePath) {
    ObjectMapper mapper = new ObjectMapper();

    try (InputStream is =
        ClaimDetailsTest.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new RuntimeException("Fixture not found: " + resourcePath);
      }
      return mapper.readValue(is, ClaimDetailsFixture.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load fixture: " + resourcePath, e);
    }
  }
}
