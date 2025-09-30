package uk.gov.justice.laa.amend.claim.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.TestSecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = NotAuthorisedController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
    })
@Import(TestSecurityConfig.class)
public class NotAuthorisedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPageReturnsView() throws Exception {
        mockMvc.perform(get("/not-authorised"))
                .andExpect(status().isOk())
                .andExpect(view().name("not-authorised"));
    }
}
