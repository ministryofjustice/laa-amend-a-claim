package uk.gov.justice.laa.amend.claim.controllers;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
@ActiveProfiles("local")
class HomePageControllerIntegrationTest extends WireMockSetup {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetHomePageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("form"));
    }

    @Test
    void testSearchWithEmptyFormReturnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/")
                    .formField("providerAccountNumber", "")
                    .formField("submissionDateMonth", "")
                    .formField("submissionDateYear", "")
                    .formField("uniqueFileNumber", "")
                    .formField("caseReferenceNumber", "")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithInvalidProviderAccountNumberReturnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/")
                    .formField("providerAccountNumber", "invalid!")
                    .formField("submissionDateMonth", "")
                    .formField("submissionDateYear", "")
                    .formField("uniqueFileNumber", "")
                    .formField("caseReferenceNumber", "")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithValidProviderAccountNumberReturnsResults() throws Exception {
        mockMvc.perform(
                post("/")
                    .formField("providerAccountNumber", "0P322F")
                    .formField("submissionDateMonth", "")
                    .formField("submissionDateYear", "")
                    .formField("uniqueFileNumber", "")
                    .formField("caseReferenceNumber", "")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?providerAccountNumber=0P322F&page=1&sort=uniqueFileNumber,asc"));
    }

    @Test
    void testSearchWithPaginationParameters() throws Exception {
        mockMvc.perform(
                get("/")
                    .param("page", "1")
                    .param("providerAccountNumber", "0P322F")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("viewModel"));
    }

    @Test
    void testSearchWithInvalidUniqueFileNumber() throws Exception {
        mockMvc.perform(
                post("/")
                    .formField("providerAccountNumber", "0P322F")
                    .formField("submissionDateMonth", "")
                    .formField("submissionDateYear", "")
                    .formField("uniqueFileNumber", "invalid!")
                    .formField("caseReferenceNumber", "")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithValidSubmissionDate() throws Exception {
        mockMvc.perform(
                post("/")
                    .formField("providerAccountNumber", "0P322F")
                    .formField("submissionDateMonth", "12")
                    .formField("submissionDateYear", "2024")
                    .formField("uniqueFileNumber", "")
                    .formField("caseReferenceNumber", "")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?providerAccountNumber=0P322F&submissionDateMonth=12&submissionDateYear=2024&page=1&sort=uniqueFileNumber,asc"));
    }
}