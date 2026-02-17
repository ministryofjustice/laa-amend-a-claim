package uk.gov.justice.laa.amend.claim.config.security;

public class SecurityConstants {

    public static final String[] PUBLIC_PATHS = {
        "/actuator/**", "/logout", "/logout-success", "/css/**", "/assets/**", "/webjars/**", "/favicon.ico"
    };

    public static final String POLICY_DIRECTIVES = "default-src 'self'; "
            + "script-src 'self'; "
            + "style-src 'self'; "
            + "img-src 'self' data:; "
            + "font-src 'self'; "
            + "connect-src 'self'; "
            + "frame-ancestors 'none'; "
            + "base-uri 'self'; "
            + "form-action 'self'; "
            + "upgrade-insecure-requests";
}
