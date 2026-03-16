package uk.gov.justice.laa.amend.claim.controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = UserControllerAdvice.Enabled.class)
public class UserControllerAdvice {

    @ModelAttribute("userId")
    public UUID getUserId(@AuthenticationPrincipal OidcUser user) {
        return UUID.fromString(user.getClaim("oid"));
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Enabled {}
}
