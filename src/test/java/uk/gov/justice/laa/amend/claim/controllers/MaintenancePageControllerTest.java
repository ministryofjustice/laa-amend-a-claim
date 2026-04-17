package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafLiteralString;

@WebMvcTest(MaintenancePageController.class)
public class MaintenancePageControllerTest extends BaseControllerTest {

  @Test
  public void testOnPageLoadReturnsHomeWhenMaintenanceDisabled() throws Exception {
    when(maintenanceService.maintenanceApplies(any())).thenReturn(false);

    mockMvc
        .perform(get("/maintenance"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/"));
  }

  @Test
  public void testOnPageLoadReturnsViewWhenMaintenanceEnabled() throws Exception {
    when(maintenanceService.maintenanceApplies(any())).thenReturn(true);

    ThymeleafLiteralString pageMessage =
        new ThymeleafLiteralString("Service undergoing maintenance");
    ThymeleafLiteralString pageTitle = new ThymeleafLiteralString("Service maintenance");

    when(maintenanceService.getMessage()).thenReturn(pageMessage);

    when(maintenanceService.getTitle()).thenReturn(pageTitle);

    mockMvc
        .perform(get("/maintenance"))
        .andExpect(status().isOk())
        .andExpect(view().name("maintenance"))
        .andExpect(model().attribute("maintenanceMessage", pageMessage))
        .andExpect(model().attribute("maintenanceTitle", pageTitle));
  }
}
