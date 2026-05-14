package uk.gov.justice.laa.amend.claim.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.mappers.ClaimMapper;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.service.ClaimService;

@WebMvcTest(HomePageController.class)
public class HomePageControllerTest extends BaseControllerTest {

  @MockitoBean private ClaimService claimService;

  @MockitoBean private ClaimResultMapper claimResultMapper;

  @MockitoBean private ClaimMapper claimMapper;

  @Test
  public void testOnPageLoadReturnsView() throws Exception {
    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(request().sessionAttributeDoesNotExist("searchUrl"));
  }

  @Test
  public void testOnPageLoadWithParamsReturnsView() throws Exception {
    String expectedSearchUrl =
        "/?officeCode=123456"
            + "&submissionDateMonth=3"
            + "&submissionDateYear=2007"
            + "&uniqueFileNumber=123456/789"
            + "&caseReferenceNumber=789"
            + "&page=1"
            + "&sort=unique_file_number,asc";

    mockMvc
        .perform(
            get("/")
                .param("officeCode", "123456")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "123456/789")
                .param("caseReferenceNumber", "789"))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(model().attribute("form", hasProperty("officeCode", is("123456"))))
        .andExpect(model().attribute("form", hasProperty("submissionDateMonth", is("3"))))
        .andExpect(model().attribute("form", hasProperty("submissionDateYear", is("2007"))))
        .andExpect(model().attribute("form", hasProperty("uniqueFileNumber", is("123456/789"))))
        .andExpect(model().attribute("form", hasProperty("caseReferenceNumber", is("789"))))
        .andExpect(request().sessionAttribute("searchUrl", expectedSearchUrl));
  }

  @Test
  public void testOnPageLoadWithUnknownParamsReturnsBadRequest() throws Exception {
    mockMvc.perform(get("/").param("foo", "bar")).andExpect(status().isBadRequest());
  }

  @Test
  public void testOnPageLoadWithInvalidSortFieldRedirectsToCleanUrl() throws Exception {
    mockMvc
        .perform(get("/?officeCode=123&page=1&sort=foo,asc"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/?officeCode=123&page=1&sort=unique_file_number,asc"));
  }

  @Test
  public void testOnPageLoadWithInvalidSortDirectionRedirectsToCleanUrl() throws Exception {
    mockMvc
        .perform(get("/?officeCode=123&page=1&sort=unique_file_number,foo"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/?officeCode=123&page=1&sort=unique_file_number,asc"));
  }

  @Test
  public void testOnPageLoadWithLegacyCamelCaseSortRedirectsPreservingOtherParams()
      throws Exception {
    mockMvc
        .perform(
            get("/?officeCode=123456&uniqueFileNumber=123456/789&page=1&sort=uniqueFileNumber,asc"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            redirectedUrl(
                "/?officeCode=123456&uniqueFileNumber=123456/789&page=1&sort=unique_file_number,asc"));
  }

  @Test
  public void testOnPageLoadWithInvalidParamsReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/").param("officeCode", "12345").param("uniqueFileNumber", "§§§"))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("There is a problem")));
  }

  @Test
  public void testOnSubmitReturnsBadRequestWithViewForInvalidForm() throws Exception {
    mockMvc
        .perform(post("/").with(csrf()).param("officeCode", "").param("uniqueFileNumber", "123"))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("index"));
  }

  @Test
  public void testOnSubmitReturnsViewForValidFormWithOneField() throws Exception {
    String expectedRedirectUrl = "/?officeCode=123456&page=1&sort=unique_file_number,asc";

    mockMvc
        .perform(post("/").with(csrf()).param("officeCode", "123456"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(expectedRedirectUrl));
  }

  @Test
  public void testOnSubmitReturnsViewForValidFormWithAllFields() throws Exception {
    String expectedRedirectUrl =
        "/?officeCode=123456"
            + "&submissionDateMonth=3"
            + "&submissionDateYear=2007"
            + "&uniqueFileNumber=123456/789"
            + "&caseReferenceNumber=789"
            + "&page=1"
            + "&sort=unique_file_number,asc";

    mockMvc
        .perform(
            post("/")
                .with(csrf())
                .param("officeCode", "123456")
                .param("submissionDateMonth", "3")
                .param("submissionDateYear", "2007")
                .param("uniqueFileNumber", "123456/789")
                .param("caseReferenceNumber", "789"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(expectedRedirectUrl));
  }
}
