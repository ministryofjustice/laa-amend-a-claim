package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthRedirectController {

    /**
     * Replacement for Spring's default login page (/login) to which a user could navigate manually. A user could still
     * navigate to /auth, but would be immediately redirected to the login flow so will not see any internal page.
     */
    @GetMapping
    public void authRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/azure");
    }
}
