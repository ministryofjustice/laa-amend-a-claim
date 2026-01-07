package uk.gov.justice.laa.amend.claim.config.security;

public class SecurityConstants {
    public static final String AUTHENTICATED = "authenticated";

    public static final String[] PUBLIC_PATHS = {
        "/actuator/**",
        "/logout",
        "/logout-success",
        "/css/**",
        "/assets/**",
        "/webjars/**",
        "/favicon.ico"
    };
}
