package uk.gov.justice.laa.amend.claim.utils;

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

    
    public static int mfaMaxAttempts() { return Integer.parseInt(getOrDefault("MFA_MAX_ATTEMPTS", "5")); }
}
