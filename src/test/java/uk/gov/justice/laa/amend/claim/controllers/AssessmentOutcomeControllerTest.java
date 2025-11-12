package uk.gov.justice.laa.amend.claim.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.mappers.ClaimResultMapper;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(AssessmentOutcomeController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class AssessmentOutcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimResultMapper claimResultMapper;

    @Test
    public void testOnPageLoadReturnsView() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        MockHttpSession session = new MockHttpSession();

        String path = String.format("/submissions/%s/claims/%s/assessment-outcome", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("assessmentOutcomeForm"))
                .andExpect(view().name("assessment-outcome"));

        Assertions.assertNull(session.getAttribute("assessmentOutcome"));
        Assertions.assertNull(session.getAttribute("liabilityForVAT"));
    }

}
