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
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, String> pathVariables = (Map<String, String>) attribute;
            String submissionId = pathVariables.get("submissionId");
            String claimId = pathVariables.get("claimId");
            if (submissionId == null || claimId == null) {
                return error(response, request, "Expected path variables not found");
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                String redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
                response.sendRedirect(redirectUrl);
                return false;
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
        return error(response, request, "Expected path variables not found");
    }

    private boolean error(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        log.warn("{}: {}", request.getRequestURI(), message);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return false;
    }
}
