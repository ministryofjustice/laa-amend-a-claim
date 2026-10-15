package uk.gov.justice.laa.payments.amend.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.payments.amend.service.DummyUserSecurityService.createAuthToken;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.payments.amend.base.RedisSetup;
import uk.gov.justice.laa.payments.amend.service.MaintenanceService;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MaintenanceIntegrationTest extends RedisSetup {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClientRegistrationRepository clientRegistrationRepository;

  @MockitoBean private OAuth2AuthorizedClientRepository authorizedClientRepository;

  @MockitoBean private MaintenanceService maintenanceService;

  @BeforeEach
  void enableMaintenance() {
    when(maintenanceService.maintenanceApplies(any())).thenReturn(true);
    when(maintenanceService.maintenanceEnabled()).thenReturn(true);
    when(maintenanceService.getTitle())
        .thenReturn(new ThymeleafMessage("maintenance.default.title"));
    when(maintenanceService.getMessage())
        .thenReturn(new ThymeleafMessage("maintenance.default.message"));
  }

  @Test
  void unauthenticatedUserMustLogInToViewMaintenancePage() throws Exception {
    mockMvc.perform(get("/maintenance")).andExpect(redirectedUrl("/auth"));
  }

  @Test
  void unauthenticatedUserIsSentToLoginWithoutLooping() throws Exception {
    mockMvc.perform(get("/")).andExpect(redirectedUrl("/auth"));
    mockMvc.perform(get("/auth")).andExpect(redirectedUrl("/oauth2/authorization/azure"));
  }

  @Test
  void authenticatedUserIsRedirectedToMaintenancePage() throws Exception {
    mockMvc
        .perform(get("/").with(authentication(createAuthToken(Set.of()))))
        .andExpect(redirectedUrl("/maintenance"));
  }

  @Test
  void authenticatedUserCanViewMaintenancePage() throws Exception {
    mockMvc
        .perform(get("/maintenance").with(authentication(createAuthToken(Set.of()))))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/maintenance"));
  }
}
