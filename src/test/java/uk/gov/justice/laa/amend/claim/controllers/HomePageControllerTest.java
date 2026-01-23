package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.client.config.SearchProperties;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.SortDirection;
import uk.gov.justice.laa.amend.claim.models.Sorts;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
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

    @MockitoBean
    private ClaimMapper claimMapper;

    @MockitoBean
    private SearchProperties searchProperties;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        Sorts expectedSorts = Sorts
            .builder()
            .value(Map.of("uniqueFileNumber", SortDirection.ASCENDING))
            .enabled(true)
            .build();

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("sorts", equalTo(expectedSorts)));
    }

    @Test
    public void testOnPageLoadReturnsViewWithDefinedSort() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        Sorts expectedSorts = Sorts
            .builder()
            .value(Map.of("caseReferenceNumber", SortDirection.DESCENDING))
            .enabled(true)
            .build();

        mockMvc.perform(get("/?sort=caseReferenceNumber,desc"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("sorts", equalTo(expectedSorts)));
    }

    @Test
    public void testOnPageLoadReturnsViewWithSortingDisabled() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(false);

        Sorts expectedSorts = Sorts
            .builder()
            .enabled(false)
            .build();

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("sorts", equalTo(expectedSorts)));
    }

    @Test
    public void testOnPageLoadWithParamsReturnsView() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(get("/")
                .param("providerAccountNumber", "12345")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "REF001")
                .param("caseReferenceNumber", "789")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("form", hasProperty("providerAccountNumber", is("12345"))))
            .andExpect(model().attribute("form", hasProperty("submissionDateMonth", is("3"))))
            .andExpect(model().attribute("form", hasProperty("submissionDateYear", is("2007"))))
            .andExpect(model().attribute("form", hasProperty("uniqueFileNumber", is("REF001"))))
            .andExpect(model().attribute("form", hasProperty("caseReferenceNumber", is("789"))));
    }

    @Test
    public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

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
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "12345")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?providerAccountNumber=12345&page=1&sort=uniqueFileNumber,asc"));
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithAllFields() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(true);

        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "12345")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "456")
                .param("caseReferenceNumber", "789")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?providerAccountNumber=12345&submissionDateMonth=3&submissionDateYear=2007&uniqueFileNumber=456&caseReferenceNumber=789&page=1&sort=uniqueFileNumber,asc"));;
    }

    @Test
    public void testOnSubmitReturnsViewForValidFormWithSortingDisabled() throws Exception {
        when(searchProperties.isSortEnabled()).thenReturn(false);

        mockMvc.perform(post("/")
                .with(csrf())
                .param("providerAccountNumber", "12345")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/?providerAccountNumber=12345&page=1"));
    }
}
