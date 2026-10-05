package uk.gov.justice.laa.payments.amend.controllers.amendments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.dstew.payments.claimsdata.model.ClaimStatus.VOID;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.getAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveAmendmentErrors;
import static uk.gov.justice.laa.payments.amend.utils.SessionUtils.saveClaim;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.payments.amend.controllers.BaseControllerTest;
import uk.gov.justice.laa.payments.amend.models.AmendmentError;
import uk.gov.justice.laa.payments.amend.models.ClaimDetails;
import uk.gov.justice.laa.payments.amend.resources.MockClaimsFunctions;
import uk.gov.justice.laa.payments.amend.viewmodels.ThymeleafMessage;
import uk.gov.justice.laa.payments.amend.viewmodels.amendmenterrors.AmendmentErrorRow;

@WebMvcTest(AmendmentsCannotBeSubmittedController.class)
class AmendmentsCannotBeSubmittedControllerTest extends BaseControllerTest {

  private static final String CLAIM_MODIFIED_MESSAGE =
      "The claim has changed since it was loaded. Review the latest claim details and try again.";

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private ClaimDetails claim;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();

    claim = MockClaimsFunctions.createMockCivilClaim();
    claim.setSubmissionId(submissionId);
    claim.setClaimId(claimId);
    saveClaim(session, claimId, claim);
  }

  @Test
  void listsErrorsInTabOrderWithNonFieldErrorsAgainstSubmission() throws Exception {
    saveAmendmentErrors(
        session,
        claimId,
        List.of(
            new AmendmentError(
                "INVALID_DISBURSEMENTS_VAT",
                "Disbursements VAT amount has exceeded the maximum accepted value",
                "disbursements_vat_amount"),
            new AmendmentError(
                "INVALID_FSP_VALIDATION_FAILURE", "The fee calculation failed validation", null),
            new AmendmentError(
                "INVALID_CASE_START_DATE",
                "Case start date is too far in the past",
                "case_start_date"),
            new AmendmentError(
                "INVALID_CLIENT_DATE_OF_BIRTH",
                "Client date of birth must be between 01/01/1900 and today",
                "client_date_of_birth")));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/cannot-be-submitted"))
        .andExpect(model().attribute("submissionId", submissionId))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(
            model()
                .attribute(
                    "errorRows",
                    List.of(
                        row(
                            "claimField.DATE_OF_BIRTH",
                            "Client date of birth must be between 01/01/1900 and today"),
                        row("claimField.CASE_START_DATE", "Case start date is too far in the past"),
                        row(
                            "claimField.DISBURSEMENTS_VAT",
                            "Disbursements VAT amount has exceeded the maximum accepted value"),
                        row(
                            "amendments.cannotBeSubmitted.submission",
                            "The fee calculation failed validation"))))
        .andExpect(content().string(containsString("Your amendments cannot be submitted")))
        .andExpect(
            content()
                .string(
                    containsString(
                        "You must fix the following issues before submitting your amendments.")))
        .andExpect(content().string(containsString(">Date of birth</th>")))
        .andExpect(content().string(containsString(">Submission</th>")))
        .andExpect(
            content()
                .string(
                    containsString(
                        "/submissions/%s/claims/%s/amendments/client"
                            .formatted(submissionId, claimId))))
        .andExpect(content().string(containsString("Amend claim")))
        .andExpect(content().string(containsString("Cancel amendments")));

    assertThatAmendmentErrorsWereCleared();
  }

  @Test
  void listsErrorForUnrecognisedFieldAgainstSubmission() throws Exception {
    saveAmendmentErrors(
        session,
        claimId,
        List.of(new AmendmentError("SOME_CODE", "Something went wrong", "not_a_claim_field")));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(
            model()
                .attribute(
                    "errorRows",
                    List.of(
                        row("amendments.cannotBeSubmitted.submission", "Something went wrong"))));
  }

  @Test
  void listsLegalHelpMatterTypeErrorAgainstMatterType() throws Exception {
    saveAmendmentErrors(
        session,
        claimId,
        List.of(
            new AmendmentError(
                "INVALID_MATTER_TYPE", "Matter type is invalid", "matter_type_code")));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(
            model()
                .attribute(
                    "errorRows",
                    List.of(row("claimField.MATTER_TYPE_CODE", "Matter type is invalid"))));
  }

  @ParameterizedTest
  @ValueSource(strings = {"CLAIM_VERSION_CONFLICT", "INVALID_VOIDED_CLAIM_NOT_AMENDABLE"})
  void showsClaimModifiedPageWhenClaimChangedByAnotherUser(String code) throws Exception {
    session.setAttribute("searchUrl", "/?providerAccount=0P322F&page=2");
    saveAmendmentErrors(
        session, claimId, List.of(new AmendmentError(code, CLAIM_MODIFIED_MESSAGE, "version")));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/amendments/claim-modified"))
        .andExpect(model().attribute("submissionId", submissionId))
        .andExpect(model().attribute("claimId", claimId))
        .andExpect(model().attribute("searchUrl", "/?providerAccount=0P322F&page=2"))
        .andExpect(model().attributeDoesNotExist("errorRows"))
        .andExpect(content().string(containsString("Amendments cannot be submitted")))
        .andExpect(
            content().string(containsString("This claim has been modified by another user.")))
        .andExpect(content().string(containsString("view claim details")))
        .andExpect(content().string(containsString("search results")))
        .andExpect(content().string(containsString("/?providerAccount=0P322F&amp;page=2")));

    assertThatAmendmentErrorsWereCleared();
  }

  @Test
  void claimModifiedPageLinksToHomeWhenNoPreviousSearch() throws Exception {
    saveAmendmentErrors(
        session,
        claimId,
        List.of(new AmendmentError("CLAIM_VERSION_CONFLICT", CLAIM_MODIFIED_MESSAGE, "version")));

    mockMvc
        .perform(get(buildPath()).session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("searchUrl", "/"));
  }

  @Test
  void returnsNotFoundWhenNoAmendmentErrorsInSession() throws Exception {
    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void returnsNotFoundForNonValidClaim() throws Exception {
    claim.setStatus(VOID);
    saveAmendmentErrors(
        session,
        claimId,
        List.of(new AmendmentError("CLAIM_VERSION_CONFLICT", CLAIM_MODIFIED_MESSAGE, "version")));

    mockMvc.perform(get(buildPath()).session(session)).andExpect(status().isNotFound());
  }

  private static AmendmentErrorRow row(String itemKey, String issue) {
    return new AmendmentErrorRow(new ThymeleafMessage(itemKey), issue);
  }

  private void assertThatAmendmentErrorsWereCleared() {
    assertThat(getAmendmentErrors(session, claimId)).isEmpty();
  }

  private String buildPath() {
    return "/submissions/%s/claims/%s/amendments/cannot-submit".formatted(submissionId, claimId);
  }
}
