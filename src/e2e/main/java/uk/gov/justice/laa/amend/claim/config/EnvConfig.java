package uk.gov.justice.laa.amend.claim.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static String getOrDefault(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    private static boolean getBooleanOrDefault(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getOrDefault(key, String.valueOf(defaultValue)));
    }

    public static String baseUrl() {
        return getOrDefault("UI_BASE_URL", "http://localhost:8080/");
    }

    public static String username() {
        return getOrDefault("P_USERNAME", "standard_user");
    }

    public static String password() {
        return getOrDefault("P_PASSWORD", "secret_sauce");
    }

    public static String browser() {
        return getOrDefault("BROWSER", "chromium");
    }

    public static boolean silasAuthenticationEnabled() {
        return getBooleanOrDefault("SILAS_AUTH_ENABLED", false);
    }

    public static boolean headless() {
        return getBooleanOrDefault("HEADLESS", true);
    }

    public static String apiBase() {
        return getOrDefault("API_BASE", "https://reqres.in");
    }

    public static String mfaSecret() {
        return getOrDefault("P_MFA_SECRET", "");
    }

    public static String dbConnectionUrl() {
        return getOrDefault("DB_CONNECTION_URL", "jdbc:postgresql://localhost:5432/dbname");
    }

    public static String dbUser() {
        return getOrDefault("DB_USER", "postgres");
    }

    public static String dbPassword() {
        return getOrDefault("DB_PASSWORD", "");
    }

    public static String userId() {
        return "LAA-Amend-A-Claim-E2E-Tests";
    }

    public static boolean axeEnabled() {
        return getBooleanOrDefault("AXE_ENABLED", false);
    }

    public static String axeReportsDirectory() {
        return "build/axe-reports/json";
    }

    public static String zapUrl() {
        return getOrDefault("ZAP_URL", "http://localhost:8090");
    }

    public static String host() {
        return getOrDefault("HOST", "localhost");
    }

    public static boolean zapEnabled() {
        return getBooleanOrDefault("ZAP_ENABLED", false);
    }
}
