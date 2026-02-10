package uk.gov.justice.laa.amend.claim.controllers;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.handlers.ClaimStatusHandler;
import uk.gov.justice.laa.amend.claim.models.AllowedClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(ChangeAllowedTotalsController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
class ChangeAllowedTotalsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceService maintenanceService;

    private MockHttpSession session;
    private String claimId;
    private String submissionId;
    private CivilClaimDetails civilClaim;
    private CrimeClaimDetails crimeClaim;
    private final ClaimStatusHandler claimStatusHandler = new ClaimStatusHandler();

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
        submissionId = "test-submission-456";
        claimId = "test-civil-claim-123";
        civilClaim = MockClaimsFunctions.createMockCivilClaim();
        crimeClaim = MockClaimsFunctions.createMockCrimeClaim();
    }

    @Test
    void testGetReturnsView_CivilClaim() throws Exception {
        civilClaim.setAllowedTotalVat(AllowedClaimField.builder().build());
        civilClaim.setAllowedTotalInclVat(AllowedClaimField.builder().build());
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().attribute("form", hasProperty("allowedTotalVat", nullValue())))
                .andExpect(model().attribute("form", hasProperty("allowedTotalInclVat", nullValue())));
    }

    @Test
    void testGetReturnsView_CrimeClaim() throws Exception {
        crimeClaim.setAllowedTotalVat(AllowedClaimField.builder().build());
        crimeClaim.setAllowedTotalInclVat(AllowedClaimField.builder().build());
        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().attribute("form", hasProperty("allowedTotalVat", nullValue())))
                .andExpect(model().attribute("form", hasProperty("allowedTotalInclVat", nullValue())));
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CivilClaim() throws Exception {
        civilClaim.setAllowedTotalInclVat(MockClaimsFunctions.createAllowedTotalInclVatField());
        civilClaim.setAllowedTotalVat(MockClaimsFunctions.createAllowedTotalVatField());

        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().attribute("form", hasProperty("allowedTotalVat", is("300.00"))))
                .andExpect(model().attribute("form", hasProperty("allowedTotalInclVat", is("300.00"))));
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CrimeClaim() throws Exception {
        crimeClaim.setAllowedTotalInclVat(MockClaimsFunctions.createAllowedTotalInclVatField());
        crimeClaim.setAllowedTotalVat(MockClaimsFunctions.createAllowedTotalVatField());

        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().attribute("form", hasProperty("allowedTotalVat", is("300.00"))))
                .andExpect(model().attribute("form", hasProperty("allowedTotalInclVat", is("300.00"))));
    }

    @Test
    void testPostSavesValuesAndRedirectsWhenAssessedTotalsAreAssessable() throws Exception {
        ClaimDetails claim = crimeClaim;
        // Given the Claim Field status has been set based on outcome
        claimStatusHandler.updateFieldStatuses(claim, claim.getAssessmentOutcome());

        Assertions.assertNotNull(crimeClaim.getAllowedTotalVat());
        Assertions.assertNotNull(crimeClaim.getAllowedTotalInclVat());

        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("allowedTotalVat", "700")
                        .param("allowedTotalInclVat", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(buildRedirectPath()));

        ClaimDetails updated = (ClaimDetails) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);

        Assertions.assertEquals(
                new BigDecimal("700.00"), updated.getAllowedTotalVat().getAssessed());
        Assertions.assertEquals(
                new BigDecimal("800.00"), updated.getAllowedTotalInclVat().getAssessed());

        Assertions.assertEquals(
                new BigDecimal("300"), updated.getAssessedTotalVat().getAssessed());
        Assertions.assertEquals(
                new BigDecimal("300"), updated.getAssessedTotalInclVat().getAssessed());
    }

    @Test
    void testPostSavesValuesAndRedirectsWhenAssessedTotalsAreNotAssessable() throws Exception {
        ClaimDetails claim = crimeClaim;
        // Given the Claim Field status has been set based on outcome
        claimStatusHandler.updateFieldStatuses(claim, claim.getAssessmentOutcome());

        ClaimField assessedTotalVat = claim.getAssessedTotalVat();
        ClaimField assessedTotalInclVat = claim.getAssessedTotalInclVat();

        assessedTotalVat.setAssessable(false);
        assessedTotalInclVat.setAssessable(false);

        Assertions.assertNotNull(crimeClaim.getAllowedTotalVat());
        Assertions.assertNotNull(crimeClaim.getAllowedTotalInclVat());

        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("allowedTotalVat", "700")
                        .param("allowedTotalInclVat", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(buildRedirectPath()));

        ClaimDetails updated = (ClaimDetails) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);

        Assertions.assertEquals(
                new BigDecimal("700.00"), updated.getAllowedTotalVat().getAssessed());
        Assertions.assertEquals(
                new BigDecimal("800.00"), updated.getAllowedTotalInclVat().getAssessed());

        Assertions.assertEquals(
                new BigDecimal("700.00"), updated.getAssessedTotalVat().getAssessed());
        Assertions.assertEquals(
                new BigDecimal("800.00"), updated.getAssessedTotalInclVat().getAssessed());
    }

    @Test
    void testPostReturnsBadRequestForNegativeValue() throws Exception {
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("allowedTotalVat", "-1")
                        .param("allowedTotalInclVat", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testPostReturnsBadRequestFor3DecimalPlacesValue() throws Exception {
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("allowedTotalVat", "100.000")
                        .param("allowedTotalInclVat", "100.000"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("allowed-totals"))
                .andExpect(model().hasErrors());
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s/allowed-totals", submissionId, claimId);
    }

    private String buildRedirectPath() {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }
}
