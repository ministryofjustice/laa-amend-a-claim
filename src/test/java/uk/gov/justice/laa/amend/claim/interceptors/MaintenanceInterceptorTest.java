package uk.gov.justice.laa.amend.claim.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ExtendWith(MockitoExtension.class)
class MaintenanceInterceptorTest {

    @InjectMocks
    private MaintenanceInterceptor interceptor;

    @Mock
    private MaintenanceService service;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Object handler;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
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
