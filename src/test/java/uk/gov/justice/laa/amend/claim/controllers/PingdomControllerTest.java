package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(PingdomController.class)
public class PingdomControllerTest extends BaseControllerTest {

    @Test
    public void testHealthCheckReturnsUpWhenMaintenanceDisabled() throws Exception {
        when(maintenanceService.maintenanceEnabled()).thenReturn(false);

        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    public void testHealthCheckReturns503WhenMaintenanceEnabled() throws Exception {
        when(maintenanceService.maintenanceEnabled()).thenReturn(true);

        mockMvc.perform(get("/ping"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }
}
