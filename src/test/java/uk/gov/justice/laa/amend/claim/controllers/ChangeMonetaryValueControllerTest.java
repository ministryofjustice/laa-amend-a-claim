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
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimField;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
        Claim claim = createClaimFor(cost);
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(buildPath(cost.getPath())).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("cost", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturns404_whenFieldIsNull(Cost cost) throws Exception {
        Claim claim = createClaimWithNullFieldFor(cost);
        session.setAttribute(claimId, claim);

        String expectedRedirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(get(buildPath(cost.getPath())).session(session))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsViewWhenQuestionAlreadyAnswered(Cost cost) throws Exception {
        Claim claim = createClaimFor(cost);
        ClaimField claimField = cost.getAccessor().get(claim);
        Assertions.assertNotNull(claimField);
        claimField.setAssessed(BigDecimal.valueOf(100));
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
        Claim claim = createClaimFor(cost);
        ClaimField claimField = cost.getAccessor().get(claim);
        Assertions.assertNotNull(claimField);
        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath(cost.getPath()))
                .session(session)
                .with(csrf())
                .param("value", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));

        Claim updated = (Claim) session.getAttribute(claimId);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(new BigDecimal("100.00"), cost.getAccessor().get(updated).getAssessed());
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testPostReturnsBadRequestForInvalidValue(Cost cost) throws Exception {
        Claim claim = createClaimFor(cost);
        session.setAttribute(claimId, claim);

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
        CivilClaimDetails claim = new CivilClaimDetails();
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(buildPath("travel-costs")).session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    void testPostReturnsNotFoundWhenClaimTypeMismatch() throws Exception {
        CivilClaimDetails claim = new CivilClaimDetails();
        session.setAttribute(claimId, claim);

        mockMvc.perform(post(buildPath("travel-costs"))
                .session(session)
                .with(csrf())
                .param("value", "1"))
            .andExpect(status().isNotFound());
    }

    private Claim createClaimFor(Cost cost) {
        Class<?> targetClass = cost.getAccessor().type();
        Claim claim;
        if (CivilClaimDetails.class.equals(targetClass)) {
            claim = MockClaimsFunctions.createMockCivilClaim();
        } else {
            claim = MockClaimsFunctions.createMockCrimeClaim();
        }
        ClaimField claimField = CostClaimField.builder().cost(cost).build();
        cost.getAccessor().set(claim, claimField);
        return claim;
    }

    private Claim createClaimWithNullFieldFor(Cost cost) {
        Class<?> targetClass = cost.getAccessor().type();
        Claim claim;
        if (CivilClaimDetails.class.equals(targetClass)) {
            claim = MockClaimsFunctions.createMockCivilClaim();
        } else {
            claim = MockClaimsFunctions.createMockCrimeClaim();
        }
        cost.getAccessor().set(claim, null);
        return claim;
    }

    private String buildPath(String cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost);
    }
}
