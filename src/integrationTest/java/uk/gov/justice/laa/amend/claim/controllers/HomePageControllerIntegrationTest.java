package uk.gov.justice.laa.amend.claim.controllers;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.forms.SearchForm;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
@ActiveProfiles("local")
class HomePageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String CLAIMS_RESPONSE = """
            {
                "claims": [
                    {
                        "uniqueFileNumber": "123456",
                        "caseReferenceNumber": "REF123",
                        "clientSurname": "Smith",
                        "dateSubmitted": "2024-01-01",
                        "account": "ACC001",
                        "type": "CLAIM",
                        "status": "PENDING"
                    }
                ],
                "totalElements": 1,
                "totalPages": 1,
                "pageNumber": 0
            }""";

    @BeforeEach
    void setUp() {
        WireMock.reset();
        setupClaimsApiStub();
    }

    @Test
    void testGetHomePageLoadsSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void testSearchWithEmptyFormReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/")
                        .flashAttr("form", new SearchForm("", "", "", "", "")))
                .andExpect(status().isBadRequest())
            .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithInvalidProviderAccountNumberReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/")
                        .flashAttr("form", new SearchForm("invalid!", "", "", "", "")))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithValidProviderAccountNumberReturnsResults() throws Exception {
        mockMvc.perform(post("/")
                        .flashAttr("form", new SearchForm("0P322F", "", "", "", "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?providerAccountNumber=0P322F&page=1&sort=uniqueFileNumber,asc"));
    }

    @Test
    void testSearchWithPaginationParameters() throws Exception {
        mockMvc.perform(get("/")
                        .param("page", "1")
                        .param("providerAccountNumber", "0P322F"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("viewModel"));
    }

    @Test
    void testSearchWithInvalidUniqueFileNumber() throws Exception {
        mockMvc.perform(post("/")
                        .flashAttr("form", new SearchForm("0P322F", "", "", "invalid!", "")))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("index"));
    }

    @Test
    void testSearchWithValidSubmissionDate() throws Exception {
        mockMvc.perform(post("/")
                        .flashAttr("form", new SearchForm("0P322F", "12", "2024", "", "")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?providerAccountNumber=0P322F&submissionDateMonth=12&submissionDateYear=2024&page=1&sort=uniqueFileNumber,asc"));
    }

    private void setupClaimsApiStub() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/api/v0/claims.*"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(CLAIMS_RESPONSE)));
    }
}