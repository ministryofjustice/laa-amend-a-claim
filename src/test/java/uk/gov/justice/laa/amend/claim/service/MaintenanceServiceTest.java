package uk.gov.justice.laa.amend.claim.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class MaintenanceServiceTest {

    MaintenanceService service;
    HttpServletRequest request;
    MessageSource messageSource;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        service = spy(new MaintenanceService(messageSource));
    }

    @Test
    void maintenanceAppliedWhen_enabled_bypassValueWrong() throws IOException {
        Cookie cookie = new Cookie("notBypass", "notPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        doReturn("secret").when(service).readBypassValue();

        boolean result = service.hasBypassCookie(request);

        assertFalse(result);
    }

    @Test
    void maintenanceAppliedWhen_enabled_noCookie() throws IOException {
        doReturn(true).when(service).maintenanceEnabled();
        when(request.getCookies()).thenReturn(null);

        boolean result = service.maintenanceApplies(request);

        assertTrue(result);
    }

    @Test
    void maintenanceNotAppliedWhen_disabled_noCookie() throws IOException {
        doReturn(false).when(service).maintenanceEnabled();
        when(request.getCookies()).thenReturn(null);

        boolean result = service.maintenanceApplies(request);

        assertFalse(result);
    }

    @Test
    void maintenanceNotAppliedWhen_enabled_withBypass() throws IOException {
        Cookie bypassCookie = new Cookie("bypass", "password");

        when(request.getCookies()).thenReturn(new Cookie[]{bypassCookie});

        boolean result = service.maintenanceApplies(request);

        assertFalse(result);
    }
}
