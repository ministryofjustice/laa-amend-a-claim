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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.math.BigDecimal;
import java.util.UUID;

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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setNetProfitCostsAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

           mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setNetProfitCostsAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setNetDisbursementAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setNetDisbursementAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setDisbursementsVatAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setDisbursementsVatAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setNetCounselCostsAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setNetCounselCostsAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setDetentionTravelWaitingCostsAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setDetentionTravelWaitingCostsAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            claim.setJrFormFillingAmount(BigDecimal.valueOf(100));
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            claim.setJrFormFillingAmount(value);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            FeeCalculationPatch fee = new FeeCalculationPatch();
            fee.setNetTravelCostsAmount(BigDecimal.valueOf(100));
            claim.setFeeCalculationResponse(fee);
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            FeeCalculationPatch fee = new FeeCalculationPatch();
            fee.setNetTravelCostsAmount(value);
            claim.setFeeCalculationResponse(fee);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
            mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", nullValue())));
        }

        @Test
        public void testGetReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            FeeCalculationPatch fee = new FeeCalculationPatch();
            fee.setNetWaitingCostsAmount(BigDecimal.valueOf(100));
            claim.setFeeCalculationResponse(fee);
            session.setAttribute(claimId, claim);

            mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("change-monetary-value"))
                .andExpect(model().attribute("prefix", equalTo(prefix)))
                .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
        }

        @Test
        public void testPostSavesValueAndRedirects() throws Exception {
            ClaimResponse claim = new ClaimResponse();
            BigDecimal value = new BigDecimal(100);
            session.setAttribute(claimId, claim);

            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", value.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

            FeeCalculationPatch fee = new FeeCalculationPatch();
            fee.setNetWaitingCostsAmount(value);
            claim.setFeeCalculationResponse(fee);
            Assertions.assertEquals(claim, session.getAttribute(claimId));
        }

        @Test
        public void testPostReturnsBadRequestForInvalidValue() throws Exception {
            mockMvc.perform(post(path)
                    .session(session)
                    .with(csrf())
                    .param("value", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("change-monetary-value"));
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
}
