package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testOnPageLoadWithParamsReturnsView() throws Exception {
        mockMvc.perform(get("/")
                .param("providerAccountNumber", "12345")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "REF001")
                .param("caseReferenceNumber", "789")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("searchForm", hasProperty("providerAccountNumber", is("12345"))))
            .andExpect(model().attribute("searchForm", hasProperty("submissionDateMonth", is("3"))))
            .andExpect(model().attribute("searchForm", hasProperty("submissionDateYear", is("2007"))))
            .andExpect(model().attribute("searchForm", hasProperty("uniqueFileNumber", is("REF001"))))
            .andExpect(model().attribute("searchForm", hasProperty("caseReferenceNumber", is("789"))));
    }

    @Test
    public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "")
                .param("uniqueFileNumber", "123")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithOneField() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "12345")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?page=1&providerAccountNumber=12345"));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithAllFields() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "12345")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "456")
                .param("caseReferenceNumber", "789")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?page=1&providerAccountNumber=12345&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789"));;
    }
}
