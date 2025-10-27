package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ConfirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        mockMvc.perform(get("/confirmation"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation"));
    }
}
