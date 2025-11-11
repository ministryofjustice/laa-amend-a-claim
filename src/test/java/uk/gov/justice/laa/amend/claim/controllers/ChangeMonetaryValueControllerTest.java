package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(ChangeMonetaryValueController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ChangeMonetaryValueControllerTest {

    private String submissionId;
    private String claimId;
    private String path;
    private MockHttpSession session;
    private String redirectUrl;
    private String prefix;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        submissionId = UUID.randomUUID().toString();
        claimId = UUID.randomUUID().toString();
        session = new MockHttpSession();
        redirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);
    }

    @Nested
    class ChangeProfitCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "profit-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setNetProfitCostsAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getNetProfitCostsAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeDisbursementsTests {

        @BeforeEach
        public void setup() {
            prefix = "disbursements";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setDisbursementAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getDisbursementAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeDisbursementsVatTests {

        @BeforeEach
        public void setup() {
            prefix = "disbursements-vat";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setDisbursementVatAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getDisbursementVatAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeCounselCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "counsel-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setNetCostOfCounselAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getNetCostOfCounselAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeDetentionTravelAndWaitingCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "detention-travel-and-waiting-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setTravelAndWaitingCostsAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getTravelAndWaitingCostsAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeJrFormFillingCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "jr-form-filling-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setJrFormFillingAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getJrFormFillingAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeTravelCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "travel-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setNetTravelCostsAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getNetTravelCostsAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class ChangeWaitingCostsTests {

        @BeforeEach
        public void setup() {
            prefix = "waiting-costs";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testGetReturnsView() throws Exception {
            getReturnsView();
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            getReturnsViewWhenQuestionAlreadyAnswered(Assessment::setNetWaitingCostsAmount);
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            postSavesValueAndRedirects(Assessment::getNetWaitingCostsAmount);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            postReturnsBadRequestForInvalidValue();
        }
    }

    @Nested
    class PageNotFoundTests {

        @BeforeEach
        public void setup() {
            prefix = "foo";
            path = String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, prefix);
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "1")
                )
                .andExpect(status().isNotFound());
        }
    }

    private void getReturnsView() throws Exception {
        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo(prefix)))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    private void getReturnsViewWhenQuestionAlreadyAnswered(BiConsumer<Assessment, BigDecimal> f) throws Exception {
        Assessment assessment = new Assessment();
        f.accept(assessment, BigDecimal.valueOf(100));
        session.setAttribute(String.format("%s:assessment", claimId), assessment);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo(prefix)))
            .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
    }

    private void postSavesValueAndRedirects(Function<Assessment, BigDecimal> f) throws Exception {
        session.setAttribute(String.format("%s:assessment", claimId), new Assessment());

        mockMvc.perform(post(path)
                .session(session)
                .with(csrf())
                .param("value", "100")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(redirectUrl));

        Assessment assessment = (Assessment) session.getAttribute(String.format("%s:assessment", claimId));

        Assertions.assertNotNull(assessment);
        Assertions.assertEquals(new BigDecimal("100.00"), f.apply(assessment));
    }

    private void postReturnsBadRequestForInvalidValue() throws Exception {
        mockMvc.perform(
                post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("change-monetary-value"));
    }
}
