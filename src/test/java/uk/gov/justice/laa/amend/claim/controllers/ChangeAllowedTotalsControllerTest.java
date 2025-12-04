package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.AmendStatus;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles("local")
@WebMvcTest(ChangeAllowedTotalsController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
class ChangeAllowedTotalsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;
    private String claimId;
    private String submissionId;
    private CivilClaimDetails civilClaim;
    private CrimeClaimDetails crimeClaim;

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
        civilClaim.setAllowedTotalVat(null);
        civilClaim.setAllowedTotalInclVat(null);
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath())
                .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("allowed-totals"))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalVat", nullValue())))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalInclVat", nullValue())));
    }

    @Test
    void testGetReturnsView_CrimeClaim() throws Exception {
        crimeClaim.setAllowedTotalVat(null);
        crimeClaim.setAllowedTotalInclVat(null);
        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath())
                .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("allowed-totals"))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalVat", nullValue())))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalInclVat", nullValue())));
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CivilClaim() throws Exception {
        civilClaim.setAllowedTotalInclVat(MockClaimsFunctions.createClaimField(AmendStatus.AMENDABLE));
        civilClaim.setAllowedTotalVat(MockClaimsFunctions.createClaimField(AmendStatus.AMENDABLE));

        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(get(buildPath())
                .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("allowed-totals"))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalVat", is("300.00"))))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalInclVat", is("300.00"))));
    }

    @Test
    void testGetReturnsViewWhenQuestionAlreadyAnswered_CrimeClaim() throws Exception {
        crimeClaim.setAllowedTotalInclVat(MockClaimsFunctions.createClaimField(AmendStatus.AMENDABLE));
        crimeClaim.setAllowedTotalVat(MockClaimsFunctions.createClaimField(AmendStatus.AMENDABLE));

        session.setAttribute(claimId, crimeClaim);

        mockMvc.perform(get(buildPath())
                .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("allowed-totals"))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalVat", is("300.00"))))
            .andExpect(model().attribute("allowedTotalForm", hasProperty("allowedTotalInclVat", is("300.00"))));
    }

    @Test
    void testPostSavesValueAndRedirects() throws Exception {
        ClaimDetails claim = crimeClaim;

        Assertions.assertNotNull(crimeClaim.getAllowedTotalVat());
        Assertions.assertNotNull(crimeClaim.getAllowedTotalVat());

        session.setAttribute(claimId, claim);

        mockMvc.perform(
                post(buildPath())
                    .session(session)
                    .with(csrf())
                    .param("allowedTotalVat", "700")
                    .param("allowedTotalInclVat", "700")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(buildRedirectPath()));

        ClaimDetails updated = (ClaimDetails) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(new BigDecimal("700.00"), updated.getAllowedTotalVat().getAmended());
        Assertions.assertEquals(AmendStatus.AMENDABLE, updated.getAllowedTotalVat().getStatus());
    }

    @Test
    void testPostReturnsBadRequestForNegativeValue() throws Exception {
        session.setAttribute(claimId, civilClaim);

        mockMvc.perform(
                post(buildPath())
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

        mockMvc.perform(
                post(buildPath())
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
