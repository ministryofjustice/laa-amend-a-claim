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

    private static final Map<String, FieldAccessors> COST_MAPPINGS = Map.ofEntries(
        Map.entry("profit-costs", new FieldAccessors(Assessment::getNetProfitCostsAmount, Assessment::setNetProfitCostsAmount)),
        Map.entry("disbursements", new FieldAccessors(Assessment::getDisbursementAmount, Assessment::setDisbursementAmount)),
        Map.entry("disbursements-vat", new FieldAccessors(Assessment::getDisbursementVatAmount, Assessment::setDisbursementVatAmount)),
        Map.entry("counsel-costs", new FieldAccessors(Assessment::getNetCostOfCounselAmount, Assessment::setNetCostOfCounselAmount)),
        Map.entry("detention-travel-and-waiting-costs", new FieldAccessors(Assessment::getTravelAndWaitingCostsAmount, Assessment::setTravelAndWaitingCostsAmount)),
        Map.entry("jr-form-filling-costs", new FieldAccessors(Assessment::getJrFormFillingAmount, Assessment::setJrFormFillingAmount)),
        Map.entry("travel-costs", new FieldAccessors(Assessment::getNetTravelCostsAmount, Assessment::setNetTravelCostsAmount)),
        Map.entry("waiting-costs", new FieldAccessors(Assessment::getNetWaitingCostsAmount, Assessment::setNetWaitingCostsAmount))
    );

    @BeforeEach
    void setup() {
        submissionId = UUID.randomUUID().toString();
        claimId = UUID.randomUUID().toString();
        session = new MockHttpSession();
        redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
    }

    private static Stream<String> validCosts() {
        return COST_MAPPINGS.keySet().stream();
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsView(String cost) throws Exception {
        mockMvc.perform(get(buildPath(cost)))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testGetReturnsViewWhenQuestionAlreadyAnswered(String cost) throws Exception {
        FieldAccessors accessors = COST_MAPPINGS.get(cost);
        Assessment assessment = new Assessment();
        accessors.setter.accept(assessment, BigDecimal.valueOf(100));
        session.setAttribute(claimKey(), assessment);

        mockMvc.perform(get(buildPath(cost)).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo(cost)))
            .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
    }

    @ParameterizedTest
    @MethodSource("validCosts")
    void testPostSavesValueAndRedirects(String cost) throws Exception {
        session.setAttribute(claimKey(), new Assessment());

        mockMvc.perform(post(buildPath(cost))
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
    void testPostReturnsBadRequestForInvalidValue(String cost) throws Exception {
        mockMvc.perform(post(buildPath(cost))
                .session(session)
                .with(csrf())
                .param("value", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(view().name("change-monetary-value"));
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
