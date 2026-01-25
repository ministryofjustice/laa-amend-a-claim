package uk.gov.justice.laa.amend.claim.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static String getOrDefault(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    public static String baseUrl() { return getOrDefault("UI_BASE_URL", "http://localhost:8080/"); }
    public static String username() { return getOrDefault("P_USERNAME", "standard_user"); }
    public static String password() { return getOrDefault("P_PASSWORD", "secret_sauce"); }
    public static String browser() { return getOrDefault("BROWSER", "chromium"); }
    public static String silasAuthenticationEnabled() { return getOrDefault("SILAS_AUTH_ENABLED", "false"); }

    public static boolean headless() { return Boolean.parseBoolean(getOrDefault("HEADLESS", "true")); }
    public static String apiBase() { return getOrDefault("API_BASE", "https://reqres.in"); }
    public static String mfaSecret() { return getOrDefault("P_MFA_SECRET", ""); }


    public static String dbConnectionUrl() { return getOrDefault("DB_CONNECTION_URL", "jdbc:postgresql://localhost:5432/dbname"); }
    public static String dbUser() { return getOrDefault("DB_USER", "postgres"); }
    public static String dbPassword() { return getOrDefault("DB_PASSWORD", ""); }
    public static String userId() {
        return "LAA-Amend-A-Claim-E2E-Tests";
    }
}