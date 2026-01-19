package uk.gov.justice.laa.amend.claim.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String getOrDefault(String key, String defaultValue) {
        String v = dotenv.get(key);
        return v != null ? v : defaultValue;
    }

    public static String baseUrl() { return getOrDefault("UI_BASE_URL", "http://localhost:8080/"); }
    public static String username() { return getOrDefault("P_USERNAME", "standard_user"); }
    public static String password() { return getOrDefault("P_PASSWORD", "secret_sauce"); }
    public static String browser() { return getOrDefault("BROWSER", "chromium"); }

    public static boolean headless() { return Boolean.parseBoolean(getOrDefault("HEADLESS", "true")); }
    public static String apiBase() { return getOrDefault("API_BASE", "https://reqres.in"); }
    public static String mfaSecret() { return getOrDefault("P_MFA_SECRET", ""); }

    public static boolean useMocks() { return Boolean.parseBoolean(getOrDefault("USE_MOCKS", "false")); }

    public static String dbHost() { return getOrDefault("DB_HOST", "localhost"); }
    public static int dbPort() {
        return Integer.parseInt(getOrDefault("DB_PORT", "5440"));
    }
    public static String dbName() { return getOrDefault("DB_NAME", "postgres"); }
    public static String dbUser() { return getOrDefault("DB_USER", "postgres"); }
    public static String dbPassword() { return getOrDefault("DB_PASSWORD", ""); }
    public static String userId() {
        return "LAA-Amend-A-Claim-E2E-Tests";
    }
}