package uk.gov.justice.laa.amend.claim.controllers;

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
import uk.gov.justice.laa.amend.claim.models.Assessment;
import uk.gov.justice.laa.amend.claim.models.OutcomeType;
import uk.gov.justice.laa.amend.claim.service.ClaimService;
import uk.gov.justice.laa.amend.claim.service.ClaimTableRowService;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimValuesTableRow;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimResponse;
import uk.gov.justice.laa.dstew.payments.claimsdata.model.FeeCalculationPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@WebMvcTest(ClaimReviewController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class ClaimReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @MockitoBean
    private ClaimTableRowService claimTableRowService;

    @Test
    public void testOnPageLoadReturnsViewWhenAssessmentInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.setFeeCalculationResponse(new FeeCalculationPatch());

        Assessment assessment = new Assessment();
        assessment.setOutcome(OutcomeType.REDUCED);

        List<ClaimValuesTableRow> tableRows = new ArrayList<>();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("application", assessment);

        when(claimService.getClaim(anyString(), anyString())).thenReturn(claimResponse);
        when(claimTableRowService.buildTableRows(any(ClaimResponse.class))).thenReturn(tableRows);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("review-and-amend"))
            .andExpect(model().attributeExists("headers"))
            .andExpect(model().attributeExists("tableRows"))
            .andExpect(model().attributeExists("assessment"))
            .andExpect(model().attribute("claimId", claimId))
            .andExpect(model().attribute("submissionId", submissionId));
    }

    @Test
    public void testOnPageLoadRedirectsWhenAssessmentNotInSession() throws Exception {
        String submissionId = UUID.randomUUID().toString();
        String claimId = UUID.randomUUID().toString();

        ClaimResponse claimResponse = new ClaimResponse();

        MockHttpSession session = new MockHttpSession();
        // No assessment in session

        when(claimService.getClaim(anyString(), anyString())).thenReturn(claimResponse);

        String path = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);

        mockMvc.perform(get(path).session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/submissions/" + submissionId + "/claims/" + claimId));
    }
}