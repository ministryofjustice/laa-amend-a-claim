package uk.gov.justice.laa.amend.claim.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClaimInterceptorTest {

    private ClaimInterceptor interceptor;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private Object handler;

    private UUID submissionId;
    private UUID claimId;

    @BeforeEach
    void setup() {
        interceptor = new ClaimInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        handler = new Object();
        submissionId = UUID.randomUUID();
        claimId = UUID.randomUUID();
    }

    @Test
    void preHandle_shouldReturn404_whenSessionMissing() throws Exception {
        String uri = String.format("/submission/%s/claims/%s", submissionId, claimId);

        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestURI()).thenReturn(uri);

        assertFalse(interceptor.preHandle(request, response, handler));

        verify(response).sendError(eq(404), contains("session not found"));
    }

    @Test
    void preHandle_shouldReturn404_whenClaimIdMissing() throws Exception {
        String uri = String.format("/submission/%s/claims/%s", submissionId, claimId);
        Map<String, String> vars = new HashMap<>();

        when(request.getSession(false)).thenReturn(session);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(vars);
        when(request.getRequestURI()).thenReturn(uri);

        assertFalse(interceptor.preHandle(request, response, handler));

        verify(response).sendError(eq(404), contains("claim ID path variable not found"));
    }

    @Test
    void preHandle_shouldReturn404_whenClaimNotFoundInSession() throws Exception {
        String uri = String.format("/submission/%s/claims/%s", submissionId, claimId);
        Map<String, String> vars = Map.of("claimId", claimId.toString());

        when(request.getSession(false)).thenReturn(session);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(vars);
        when(request.getRequestURI()).thenReturn(uri);
        when(session.getAttribute(claimId.toString())).thenReturn(null);

        assertFalse(interceptor.preHandle(request, response, handler));

        verify(response).sendError(eq(404), contains("claim not found"));
    }

    @Test
    void preHandle_shouldReturnTrue_whenCivilClaimFound() throws Exception {
        Map<String, String> vars = Map.of("claimId", claimId.toString());
        ClaimDetails claim = new CivilClaimDetails();

        when(request.getSession(false)).thenReturn(session);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(vars);
        when(session.getAttribute(claimId.toString())).thenReturn(claim);

        assertTrue(interceptor.preHandle(request, response, handler));

        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void preHandle_shouldReturnTrue_whenCrimeClaimFound() throws Exception {
        Map<String, String> vars = Map.of("claimId", claimId.toString());
        ClaimDetails claim = new CivilClaimDetails();

        when(request.getSession(false)).thenReturn(session);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(vars);
        when(session.getAttribute(claimId.toString())).thenReturn(claim);

        assertTrue(interceptor.preHandle(request, response, handler));

        verify(response, never()).sendError(anyInt(), anyString());
    }
}