package uk.gov.justice.laa.amend.claim.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class TestDataUtils {

    public static String readClasspathResource(String resourcePath) {
        InputStream is = TestDataUtils.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Fixture not found on classpath: " + resourcePath);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read fixture: " + resourcePath, e);
        }
    }

    public static String generateUfn() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int n = (int) (System.nanoTime() % 1000);
        if (n < 0) n = -n;
        String suffix = String.format("%03d", n);
        return datePart + "/" + suffix;
    }
}
