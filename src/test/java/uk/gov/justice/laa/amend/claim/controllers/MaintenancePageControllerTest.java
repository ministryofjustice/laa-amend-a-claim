package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;

@ActiveProfiles("local")
@WebMvcTest(MaintenancePageController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class MaintenancePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @Test
    public void testOnPageLoadReturnsViewWhenMaintenanceEnabled() throws Exception {
        when(maintenanceService.maintenanceEnabled()).thenReturn(true);

        ThymeleafLiteralString message = new ThymeleafLiteralString("Foo");
        ThymeleafLiteralString title = new ThymeleafLiteralString("Bar");

        when(maintenanceService.getMessage()).thenReturn(message);

        when(maintenanceService.getTitle()).thenReturn(title);

        mockMvc.perform(get("/maintenance"))
                .andExpect(status().isOk())
                .andExpect(view().name("maintenance"))
                .andExpect(model().attribute("maintenanceMessage", message))
                .andExpect(model().attribute("maintenanceTitle", title));
    }

    @Test
    public void testOnPageLoadReturnsNotFoundWhenMaintenanceDisabled() throws Exception {
        when(maintenanceService.maintenanceEnabled()).thenReturn(false);

        mockMvc.perform(get("/maintenance")).andExpect(status().isNotFound());

        verify(maintenanceService, never()).getMessage();
        verify(maintenanceService, never()).getTitle();
    }
}
