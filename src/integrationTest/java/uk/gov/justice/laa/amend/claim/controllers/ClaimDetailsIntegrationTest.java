package uk.gov.justice.laa.amend.claim.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.base.WireMockSetup.setupGetClaimStub;
import static uk.gov.justice.laa.amend.claim.base.WireMockSetup.setupGetProviderOfficeStub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.viewmodels.ClaimDetailsView;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClaimDetailsIntegrationTest extends ControllerIntegrationTest {

    private static final String SUBMISSION_ID = "c8f2c0d4-97b1-4c4a-96f2-4dd62a4e6aa2";
    private static final String CLAIM_ID = "3f8a0ac4-2f63-4ed2-8bfb-2eb0fc0ba330";
    private static final String OFFICE_CODE = "0P322F";
    private static final String FIRM_NAME = "Test Firm";

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        setupGetClaimStub(SUBMISSION_ID, CLAIM_ID, OFFICE_CODE);
        setupGetProviderOfficeStub(OFFICE_CODE, FIRM_NAME);
    }

    @Test
    void testClaimDetailsPageLoadsSuccessfully() throws Exception {
        var result = mockMvc.perform(get("/submissions/{submissionId}/claims/{claimId}", SUBMISSION_ID, CLAIM_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("claim-summary"))
                .andExpect(model().attributeExists("claim"))
                .andExpect(model().attributeExists("submissionId"))
                .andExpect(model().attributeExists("claimId"))
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        Assertions.assertNotNull(modelAndView);
        @SuppressWarnings("unchecked")
        ClaimDetailsView<ClaimDetails> claim =
                (ClaimDetailsView<ClaimDetails>) modelAndView.getModel().get("claim");
        assertThat(claim).isNotNull();
        assertThat(claim.claim().getProviderName()).isEqualTo(FIRM_NAME);
    }
}
