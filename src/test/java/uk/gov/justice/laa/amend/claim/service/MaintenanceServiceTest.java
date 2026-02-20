package uk.gov.justice.laa.amend.claim.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;

@ExtendWith(MockitoExtension.class)
public class MaintenanceServiceTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ConfigurableMaintenanceService service;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;

    @Test
    void getMessageWhenMessageExistsInConfig() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("Foo");

            ThymeleafLiteralString result = (ThymeleafLiteralString) service.getMessage();

            Assertions.assertEquals("Foo", result.getValue());

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()));

            Assertions.assertEquals(
                    "/config/maintenance/message", pathCaptor.getValue().toString());
        }
    }

    @Test
    void getMessageWhenMessageDoesExistInConfig() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);

            ThymeleafMessage result = (ThymeleafMessage) service.getMessage();

            Assertions.assertEquals("maintenance.default.message", result.getKey());
        }
    }

    @Test
    void getTitleWhenTitleExistsInConfig() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("Foo");

            ThymeleafLiteralString result = (ThymeleafLiteralString) service.getTitle();

            Assertions.assertEquals("Foo", result.getValue());

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()));

            Assertions.assertEquals(
                    "/config/maintenance/title", pathCaptor.getValue().toString());
        }
    }

    @Test
    void getTitleWhenTitleDoesExistInConfig() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);

            ThymeleafMessage result = (ThymeleafMessage) service.getTitle();

            Assertions.assertEquals("maintenance.default.title", result.getKey());
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledDoesNotExistInConfig() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);

            boolean result = service.maintenanceApplies(request);

            Assertions.assertFalse(result);
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledDoesExistInConfigAndIsDisabled() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("false");

            boolean result = service.maintenanceApplies(request);

            Assertions.assertFalse(result);

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()));

            Assertions.assertEquals(
                    "/config/maintenance/enabled", pathCaptor.getValue().toString());
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledDoesExistInConfigAndIsEnabledAndNullCookies() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("true");

            when(request.getCookies()).thenReturn(null);

            boolean result = service.maintenanceApplies(request);

            Assertions.assertTrue(result);

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()));

            Assertions.assertEquals(
                    "/config/maintenance/enabled", pathCaptor.getValue().toString());
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledDoesExistInConfigAndIsEnabledAndHasNoBypassCookie() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("true").thenReturn("password");

            when(request.getCookies()).thenReturn(new Cookie[] {});

            boolean result = service.maintenanceApplies(request);

            Assertions.assertTrue(result);

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()), times(2));

            List<Path> paths = pathCaptor.getAllValues();

            Assertions.assertEquals("/config/maintenance/enabled", paths.get(0).toString());
            Assertions.assertEquals(
                    "/config/maintenance/bypassPassword", paths.get(1).toString());
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledDoesExistInConfigAndIsEnabledAndHasBypassCookie() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new MockCookie("bypass_cookie", "password"));

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("true").thenReturn("password");

            boolean result = service.maintenanceApplies(request);

            Assertions.assertFalse(result);

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()), times(2));

            List<Path> paths = pathCaptor.getAllValues();

            Assertions.assertEquals("/config/maintenance/enabled", paths.get(0).toString());
            Assertions.assertEquals(
                    "/config/maintenance/bypassPassword", paths.get(1).toString());
        }
    }

    @Test
    void maintenanceAppliesThrowsExceptionWhenEnabledDoesExistInConfigAndIsEnabledButFailsToGetPasswordFromConfig() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new MockCookie("bypass_cookie", "password"));

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles.when(() -> Files.readString(any())).thenReturn("true").thenThrow(new IOException());

            Assertions.assertThrows(IOException.class, () -> service.maintenanceApplies(request));

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()), times(2));

            List<Path> paths = pathCaptor.getAllValues();

            Assertions.assertEquals("/config/maintenance/enabled", paths.get(0).toString());
            Assertions.assertEquals(
                    "/config/maintenance/bypassPassword", paths.get(1).toString());
        }
    }

    @Test
    void maintenanceAppliesWhenEnabledCheckFailsAndHasBypassCookie() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new MockCookie("bypass_cookie", "password"));

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(true);

            mockedFiles
                    .when(() -> Files.readString(any()))
                    .thenThrow(new IOException())
                    .thenReturn("password");

            boolean result = service.maintenanceApplies(request);

            Assertions.assertFalse(result);

            mockedFiles.verify(() -> Files.readString(pathCaptor.capture()), times(2));

            List<Path> paths = pathCaptor.getAllValues();

            Assertions.assertEquals("/config/maintenance/enabled", paths.get(0).toString());
            Assertions.assertEquals(
                    "/config/maintenance/bypassPassword", paths.get(1).toString());
        }
    }
}
