package uk.gov.justice.laa.amend.claim.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;
import uk.gov.justice.laa.amend.claim.models.ClaimDetailsFixture;
import uk.gov.justice.laa.amend.claim.tests.ClaimDetailsTest;

public class E2ETestHelper {

    public static ClaimDetailsFixture loadFixture(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = ClaimDetailsTest.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Fixture not found: " + resourcePath);
            }
            return mapper.readValue(is, ClaimDetailsFixture.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load fixture: " + resourcePath, e);
        }
    }

    public static String getFromMap(Map<String, String[]> map, String key, int index) {
        String[] arr = map.get(key);
        if (arr == null) {
            throw new IllegalArgumentException("Key not found in fixture totals: " + key);
        }
        if (index < 0 || index >= arr.length) {
            throw new IllegalArgumentException("Index " + index + " out of range for key " + key);
        }
        return arr[index];
    }

    public static String normalizeMoneyForInput(String raw) {
        if (raw == null) return null;
        String s = raw.trim();

        // Treat "Not applicable" as no change (return null to skip setting the field)
        if (s.equalsIgnoreCase("Not applicable")) {
            return null;
        }

        // Remove common currency formatting
        s = s.replace("£", "").replace(",", "").trim();

        // If it contains anything other than digits or dot after stripping, consider skipping
        if (!s.matches("\\d+(\\.\\d{1,2})?")) {
            // Optionally: throw or return null depending on your tolerance
            return null;
        }

        return s;
    }
}
