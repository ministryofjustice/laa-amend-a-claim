package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.*;
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
import uk.gov.justice.laa.amend.claim.models.Assessment;
import uk.gov.justice.laa.amend.claim.models.Cost;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private static final Map<Cost, FieldAccessors> COST_MAPPINGS = Map.ofEntries(
        Map.entry(Cost.PROFIT_COSTS, new FieldAccessors(Assessment::getNetProfitCostsAmount, Assessment::setNetProfitCostsAmount)),
        Map.entry(Cost.DISBURSEMENTS, new FieldAccessors(Assessment::getDisbursementAmount, Assessment::setDisbursementAmount)),
        Map.entry(Cost.DISBURSEMENTS_VAT, new FieldAccessors(Assessment::getDisbursementVatAmount, Assessment::setDisbursementVatAmount)),
        Map.entry(Cost.COUNSEL_COSTS, new FieldAccessors(Assessment::getNetCostOfCounselAmount, Assessment::setNetCostOfCounselAmount)),
        Map.entry(Cost.DETENTION_TRAVEL_AND_WAITING_COSTS, new FieldAccessors(Assessment::getTravelAndWaitingCostsAmount, Assessment::setTravelAndWaitingCostsAmount)),
        Map.entry(Cost.JR_FORM_FILLING_COSTS, new FieldAccessors(Assessment::getJrFormFillingAmount, Assessment::setJrFormFillingAmount)),
        Map.entry(Cost.TRAVEL_COSTS, new FieldAccessors(Assessment::getNetTravelCostsAmount, Assessment::setNetTravelCostsAmount)),
        Map.entry(Cost.WAITING_COSTS, new FieldAccessors(Assessment::getNetWaitingCostsAmount, Assessment::setNetWaitingCostsAmount))
    );

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID().toString();
        claimId = UUID.randomUUID().toString();
        session = new MockHttpSession();
        redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
    }

    private static Stream<Cost> validCosts() {
        return COST_MAPPINGS.keySet().stream();
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsView(Cost cost) throws Exception {
        mockMvc.perform(get(buildPath(cost.getPath())))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("cost", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsViewWhenQuestionAlreadyAnswered(Cost cost) throws Exception {
        FieldAccessors accessors = COST_MAPPINGS.get(cost);
        Assessment assessment = new Assessment();
        accessors.setter.accept(assessment, BigDecimal.valueOf(100));
        session.setAttribute(claimKey(), assessment);

        mockMvc.perform(get(buildPath(cost.getPath())).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("cost", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testPostSavesValueAndRedirects(Cost cost) throws Exception {
        session.setAttribute(claimKey(), new Assessment());

        mockMvc.perform(post(buildPath(cost.getPath()))
                .session(session)
                .with(csrf())
                .param("value", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));

        Assessment updated = (Assessment) session.getAttribute(claimKey());
        FieldAccessors accessors = COST_MAPPINGS.get(cost);

        Assertions.assertNotNull(updated);
        Assertions.assertEquals(new BigDecimal("100.00"), accessors.getter.apply(updated));
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
    void testPageNotFound() throws Exception {
        mockMvc.perform(post(buildPath("foo"))
                .session(session)
                .with(csrf())
                .param("value", "1"))
            .andExpect(status().isNotFound());
    }

    private String buildPath(String cost) {
        return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost);
    }

    private String claimKey() {
        return String.format("%s:assessment", claimId);
    }

    private record FieldAccessors(
        Function<Assessment, BigDecimal> getter,
        BiConsumer<Assessment, BigDecimal> setter
    ) {}
}
