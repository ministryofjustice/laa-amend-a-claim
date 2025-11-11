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
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

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

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        submissionId = UUID.randomUUID().toString();
        claimId = UUID.randomUUID().toString();
        path = String.format("/submissions/%s/claims/%s/profit-costs", submissionId, claimId);
        session = new MockHttpSession();
    }

    @Test
    public void testGetProfitCostsReturnsView() throws Exception {
        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo("profitCosts")))
            .andExpect(model().attribute("form", hasProperty("value", nullValue())));
    }

    @Test
    public void testGetProfitCostsReturnsViewWhenQuestionAlreadyAnswered() throws Exception {
        ClaimResponse claim = new ClaimResponse();
        BigDecimal value = new BigDecimal(100);
        claim.setNetProfitCostsAmount(value);
        session.setAttribute(claimId, claim);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("change-monetary-value"))
            .andExpect(model().attribute("prefix", equalTo("profitCosts")))
            .andExpect(model().attribute("form", hasProperty("value", is(value))));
    }

    @Test
    public void testPostProfitCostsSavesValueAndRedirects() throws Exception {
        ClaimResponse claim = new ClaimResponse();
        BigDecimal value = new BigDecimal(100);
        session.setAttribute(claimId, claim);

        String expectedRedirectUrl = String.format("/submissions/%s/claims/%s", submissionId, claimId);

        mockMvc.perform(post(path)
                .session(session)
                .with(csrf())
                .param("value", value.toString())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(expectedRedirectUrl));

        claim.setNetProfitCostsAmount(value);
        Assertions.assertEquals(claim, session.getAttribute(claimId));
    }

    @Test
    public void testPostProfitCostsReturnsBadRequestForInvalidValue() throws Exception {
        mockMvc.perform(post(path)
                .session(session)
                .with(csrf())
                .param("value", "-1")
            )
            .andExpect(status().isBadRequest())
            .andExpect(view().name("change-monetary-value"));
    }
}
