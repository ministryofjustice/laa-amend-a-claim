package uk.gov.justice.laa.payments.amend.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.payments.amend.base.RedisSetup;
import uk.gov.justice.laa.payments.amend.service.MaintenanceService;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("ephemeral")
class EphemeralMaintenanceIntegrationTest extends RedisSetup {

  @Autowired private MockMvc mockMvc;

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
  void maintenancePageRendersAfterSignOut() throws Exception {
    mockMvc.perform(post("/logout").with(csrf())).andExpect(redirectedUrl("/login?logout"));
    mockMvc.perform(get("/login?logout")).andExpect(redirectedUrl("/maintenance"));

    // no session cookie, as after sign out
    mockMvc
        .perform(get("/maintenance"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/maintenance"));
  }
}
