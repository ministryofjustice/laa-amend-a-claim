package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.viewmodels.CivilClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimFieldRow;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimSummary;
import uk.gov.justice.laa.amend.claim.viewmodels.CrimeClaimSummary;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(ChangeMonetaryValueController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
class ChangeMonetaryValueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String submissionId;
    private String claimId;
    private MockHttpSession session;
    private String redirectUrl;

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID().toString();
        claimId = UUID.randomUUID().toString();
        session = new MockHttpSession();
        redirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
    }

    private static Stream<Cost> validCosts() {
        return Arrays.stream(Cost.values());
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsView(Cost cost) throws Exception {
        ClaimSummary claim = createClaimSummaryFor(cost);
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(buildPath(cost.getPath())).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("cost", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsViewWhenQuestionAlreadyAnswered(Cost cost) throws Exception {
        ClaimSummary claim = createClaimSummaryFor(cost);
        ClaimFieldRow claimField = cost.getAccessor().get(claim);
        Assertions.assertNotNull(claimField);
        claimField.setAmended(BigDecimal.valueOf(100));
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(buildPath(cost.getPath())).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("cost", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testPostSavesValueAndRedirects(Cost cost) throws Exception {
        ClaimSummary claim = createClaimSummaryFor(cost);
        ClaimFieldRow claimField = cost.getAccessor().get(claim);
        Assertions.assertNotNull(claimField);
        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath(cost.getPath()))
                .session(session)
                .with(csrf())
                .param("value", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));

        ClaimSummary updated = (ClaimSummary) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(new BigDecimal("100.00"), cost.getAccessor().get(updated).getAmended());
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testPostReturnsBadRequestForInvalidValue(Cost cost) throws Exception {
        mockMvc.perform(post(buildPath(cost.getPath()))
                .session(session)
                .with(csrf())
                .param("value", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(content().string(containsString("must not be negative")));
    }

    @Test
    void testGetReturnsNotFoundWhenInvalidCost() throws Exception {
        mockMvc.perform(get(buildPath("foo"))
                .session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPostReturnsNotFoundWhenInvalidCost() throws Exception {
        mockMvc.perform(post(buildPath("foo"))
                .session(session)
                .with(csrf())
                .param("value", "1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetReturnsNotFoundWhenClaimTypeMismatch() throws Exception {
        CivilClaimSummary claim = new CivilClaimSummary();
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(buildPath("travel-costs")).session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPostReturnsNotFoundWhenClaimTypeMismatch() throws Exception {
        CivilClaimSummary claim = new CivilClaimSummary();
        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath("travel-costs"))
                .session(session)
                .with(csrf())
                .param("value", "1"))
            .andExpect(status().isNotFound());
    }

    private ClaimSummary createClaimSummaryFor(Cost cost) {
        Class<?> targetClass = cost.getAccessor().type();
        ClaimSummary claim;
        if (CivilClaimSummary.class.equals(targetClass)) {
            claim =  new CivilClaimSummary();
        } else if (CrimeClaimSummary.class.equals(targetClass)) {
            claim = new CrimeClaimSummary();
        } else {
            claim = new ClaimSummary();
        }
        cost.getAccessor().set(claim, new ClaimFieldRow("", null, null, null));
        return claim;
    }

    private String buildPath(String cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost);
    }
}
