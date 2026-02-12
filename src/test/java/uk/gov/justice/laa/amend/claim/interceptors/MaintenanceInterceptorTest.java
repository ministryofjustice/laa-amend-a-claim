package uk.gov.justice.laa.amend.claim.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

class MaintenanceInterceptorTest {

    MaintenanceInterceptor interceptor;
    MaintenanceService service;
    HttpServletRequest request;
    HttpServletResponse response;
    HttpSession session;

    Object handler;

    @BeforeEach
    void setup() {
        service = spy(new MaintenanceService());
        interceptor = new MaintenanceInterceptor(service);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        handler = new Object();
    }

    @Test
    void preHandle_shouldReturnHome_whenMaintenanceIsNotApplied() throws Exception {

        when(service.maintenanceApplies(request)).thenReturn(false);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    void preHandle_shouldReturnRedirect_whenMaintenanceIsApplied() throws Exception {

        when(service.maintenanceApplies(request)).thenReturn(true);

        assertFalse(interceptor.preHandle(request, response, handler));
        verify(response).sendRedirect(request.getContextPath() + "/maintenance");
    }
}
