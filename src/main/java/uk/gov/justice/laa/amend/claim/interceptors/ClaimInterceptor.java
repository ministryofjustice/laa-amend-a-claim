package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

import java.util.Map;

@Component
public class ClaimInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("%s: session not found", request.getRequestURI()));
            return false;
        }

        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String claimId = pathVariables.get("claimId");
        if (claimId == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("%s: claim ID path variable not found", request.getRequestURI()));
            return false;
        }

        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);

        // TODO - see if claim is not escaped
        if (claim == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format("%s: claim not found", request.getRequestURI()));
            return false;
        }

        return true;
    }
}
