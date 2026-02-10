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
import uk.gov.justice.laa.amend.claim.models.AssessedClaimField;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(ChangeAssessedTotalsController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
class ChangeAssessedTotalsControllerTest {

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
        civilClaim.setAssessedTotalVat(AssessedClaimField.builder().build());
        civilClaim.setAssessedTotalInclVat(AssessedClaimField.builder().build());
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().attribute("form", hasProperty("assessedTotalVat", nullValue())))
                .andExpect(model().attribute("form", hasProperty("assessedTotalInclVat", nullValue())));
    }

    @Test
    void testGetReturnsView_CrimeClaim() throws Exception {
        crimeClaim.setAssessedTotalVat(AssessedClaimField.builder().build());
        crimeClaim.setAssessedTotalInclVat(AssessedClaimField.builder().build());
        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().attribute("form", hasProperty("assessedTotalVat", nullValue())))
                .andExpect(model().attribute("form", hasProperty("assessedTotalInclVat", nullValue())));
    }

    @Test
    void testGetRedirectsWhenStatusIsDoNotDisplay_CivilClaim() throws Exception {
        ClaimField assessedTotalVat = AssessedClaimField.builder().build();
        ClaimField assessedTotalInclVat = AssessedClaimField.builder().build();
        assessedTotalVat.setAssessable(false);
        assessedTotalInclVat.setAssessable(false);
        civilClaim.setAssessedTotalVat(assessedTotalVat);
        civilClaim.setAssessedTotalInclVat(assessedTotalInclVat);
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
    }

    @Test
    void testGetRedirectsWhenStatusIsDoNotDisplay_CrimeClaim() throws Exception {
        ClaimField assessedTotalVat = AssessedClaimField.builder().build();
        ClaimField assessedTotalInclVat = AssessedClaimField.builder().build();
        assessedTotalVat.setAssessable(false);
        assessedTotalInclVat.setAssessable(false);
        crimeClaim.setAssessedTotalVat(assessedTotalVat);
        crimeClaim.setAssessedTotalInclVat(assessedTotalInclVat);
        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CivilClaim() throws Exception {
        civilClaim.setAssessedTotalInclVat(MockClaimsFunctions.createAssessedTotalInclVatField());
        civilClaim.setAssessedTotalVat(MockClaimsFunctions.createAssessedTotalVatField());

        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().attribute("form", hasProperty("assessedTotalVat", is("300.00"))))
                .andExpect(model().attribute("form", hasProperty("assessedTotalInclVat", is("300.00"))));
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CrimeClaim() throws Exception {
        crimeClaim.setAssessedTotalInclVat(MockClaimsFunctions.createAssessedTotalInclVatField());
        crimeClaim.setAssessedTotalVat(MockClaimsFunctions.createAssessedTotalVatField());

        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath()).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().attribute("form", hasProperty("assessedTotalVat", is("300.00"))))
                .andExpect(model().attribute("form", hasProperty("assessedTotalInclVat", is("300.00"))));
    }

    @Test
    void testPostSavesValueAndRedirects() throws Exception {
        ClaimDetails claim = crimeClaim;
        claimStatusHandler.updateFieldStatuses(claim, claim.getAssessmentOutcome());
        Assertions.assertNotNull(crimeClaim.getAssessedTotalVat());
        Assertions.assertNotNull(crimeClaim.getAssessedTotalInclVat());

        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("assessedTotalVat", "700")
                        .param("assessedTotalInclVat", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(buildRedirectPath()));

        ClaimDetails updated = (ClaimDetails) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);

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
                        .param("assessedTotalVat", "-1")
                        .param("assessedTotalInclVat", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testPostReturnsBadRequestFor3DecimalPlacesValue() throws Exception {
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(post(buildPath())
                        .session(session)
                        .with(csrf())
                        .param("assessedTotalVat", "100.000")
                        .param("assessedTotalInclVat", "100.000"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("assessed-totals"))
                .andExpect(model().hasErrors());
    }

    private String buildPath() {
        return String.format("/submissions/%s/claims/%s/assessed-totals", submissionId, claimId);
    }

    private String buildRedirectPath() {
        return String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }
}
