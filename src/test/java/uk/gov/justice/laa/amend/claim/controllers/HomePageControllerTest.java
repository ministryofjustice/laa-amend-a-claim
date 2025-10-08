package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testOnSubmitRedirectsWhenEmptyForm() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "")
                .param("referenceNumber", "123")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    public void testOnSubmitReturnsViewForValidForm() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "123")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }
}
