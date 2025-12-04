package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class ClaimInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return error(response, request, "Session not found");
        }

        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String claimId = pathVariables.get("claimId");
        if (claimId == null) {
            return error(response, request, "Claim ID path variable not found");
        }

        ClaimDetails claim = (ClaimDetails) session.getAttribute(claimId);
        if (claim == null) {
            return error(response, request, "Claim not found");
        }
        if (claim.getEscaped() == null || !claim.getEscaped()) {
            return error(response, request, "Claim is not an escape case");
        }

        request.setAttribute(claimId, claim);

        return true;
    }

    private boolean error(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        log.warn("{}: {}", request.getRequestURI(), message);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return false;
    }
}
