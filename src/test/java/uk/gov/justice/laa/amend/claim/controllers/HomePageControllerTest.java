package uk.gov.justice.laa.amend.claim.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.amend.claim.models.SortField;
import uk.gov.justice.laa.amend.claim.models.Sorts;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(HomePageController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    @MockitoBean
    private ClaimMapper claimMapper;

    @MockitoBean
    private SearchProperties searchProperties;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        Sorts expectedSorts = Sorts.builder()
                .value(Map.of(SortField.UNIQUE_FILE_NUMBER, SortDirection.ASCENDING))
                .enabled(true)
                .build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("sorts", equalTo(expectedSorts)))
                .andExpect(request().sessionAttributeDoesNotExist("searchUrl"));
    }

    @Test
    public void testOnPageLoadReturnsViewWithDefinedSort() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        Sorts expectedSorts = Sorts.builder()
                .value(Map.of(SortField.CASE_REFERENCE_NUMBER, SortDirection.DESCENDING))
                .enabled(true)
                .build();

        mockMvc.perform(get("/?sort=caseReferenceNumber,desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("sorts", equalTo(expectedSorts)))
                .andExpect(request().sessionAttributeDoesNotExist("searchUrl"));
    }

    @Test
    public void testOnPageLoadReturnsViewWithSortingDisabled() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(false);

        Sorts expectedSorts = Sorts.builder().enabled(false).build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("sorts", equalTo(expectedSorts)))
                .andExpect(request().sessionAttributeDoesNotExist("searchUrl"));
    }

    @Test
    public void testOnPageLoadWithParamsReturnsView() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        String expectedSearchUrl = "/?providerAccountNumber=123456"
                + "&submissionDateMonth=3"
                + "&submissionDateYear=2007"
                + "&uniqueFileNumber=123456/789"
                + "&caseReferenceNumber=789"
                + "&page=1"
                + "&sort=uniqueFileNumber,asc";

        mockMvc.perform(get("/").param("providerAccountNumber", "123456")
                        .param("submissionDateMonth", "3")
                        .param("submissionDateYear", "2007")
                        .param("uniqueFileNumber", "123456/789")
                        .param("caseReferenceNumber", "789"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("form", hasProperty("providerAccountNumber", is("123456"))))
                .andExpect(model().attribute("form", hasProperty("submissionDateMonth", is("3"))))
                .andExpect(model().attribute("form", hasProperty("submissionDateYear", is("2007"))))
                .andExpect(model().attribute("form", hasProperty("uniqueFileNumber", is("123456/789"))))
                .andExpect(model().attribute("form", hasProperty("caseReferenceNumber", is("789"))))
                .andExpect(request().sessionAttribute("searchUrl", expectedSearchUrl));
    }

    @Test
    public void testOnPageLoadWithUnknownParamsReturnsBadRequest() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(get("/").param("foo", "bar")).andExpect(status().isBadRequest());
    }

    @Test
    public void testOnPageLoadWithInvalidSortFieldReturnsBadRequest() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(get("/?providerAccountNumber=123&page=1&sort=foo,asc")).andExpect(status().isBadRequest());
    }

    @Test
    public void testOnPageLoadWithInvalidSortDirectionReturnsBadRequest() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(get("/?providerAccountNumber=123&page=1&sort=uniqueFileNumber,foo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testOnPageLoadWithInvalidParamsReturnsBadRequest() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(get("/").param("providerAccountNumber", "12345").param("uniqueFileNumber", "§§§"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("There is a problem")));
    }

    @Test
    public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(post("/")
                        .with(csrf())
                        .param("providerAccountNumber", "")
                        .param("uniqueFileNumber", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("index"));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithOneField() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        String expectedRedirectUrl = "/?providerAccountNumber=123456&page=1&sort=uniqueFileNumber,asc";

        mockMvc.perform(post("/").with(csrf()).param("providerAccountNumber", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithAllFields() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        String expectedRedirectUrl = "/?providerAccountNumber=123456"
                + "&submissionDateMonth=3"
                + "&submissionDateYear=2007"
                + "&uniqueFileNumber=123456/789"
                + "&caseReferenceNumber=789"
                + "&page=1"
                + "&sort=uniqueFileNumber,asc";

        mockMvc.perform(post("/")
                        .with(csrf())
                        .param("providerAccountNumber", "123456")
                        .param("submissionDateMonth", "3")
                        .param("submissionDateYear", "2007")
                        .param("uniqueFileNumber", "123456/789")
                        .param("caseReferenceNumber", "789"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithSortingDisabled() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(false);

        String expectedRedirectUrl = "/?providerAccountNumber=123456&page=1";

        mockMvc.perform(post("/").with(csrf()).param("providerAccountNumber", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }
}
