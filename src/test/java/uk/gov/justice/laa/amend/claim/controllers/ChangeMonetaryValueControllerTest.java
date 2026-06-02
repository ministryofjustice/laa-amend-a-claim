package uk.gov.justice.laa.amend.claim.controllers;

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
import static uk.gov.justice.laa.amend.claim.models.Cost.COUNSEL_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.DETENTION_TRAVEL_AND_WAITING_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.DISBURSEMENTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.DISBURSEMENTS_VAT;
import static uk.gov.justice.laa.amend.claim.models.Cost.JR_FORM_FILLING_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.PROFIT_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.TRAVEL_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Cost.WAITING_COSTS;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_CASEWORKER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;
import static uk.gov.justice.laa.amend.claim.utils.ChangeMonetaryValueUtils.getCostClaimField;
import static uk.gov.justice.laa.amend.claim.utils.ChangeMonetaryValueUtils.setCostClaimField;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import uk.gov.justice.laa.amend.claim.models.CivilClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Claim;
import uk.gov.justice.laa.amend.claim.models.ClaimDetails;
import uk.gov.justice.laa.amend.claim.models.Cost;
import uk.gov.justice.laa.amend.claim.models.CostClaimField;
import uk.gov.justice.laa.amend.claim.models.CrimeClaimDetails;
import uk.gov.justice.laa.amend.claim.models.MediationClaimDetails;
import uk.gov.justice.laa.amend.claim.resources.MockClaimsFunctions;
import uk.gov.justice.laa.amend.claim.utils.ChangeMonetaryValueUtils;

@WebMvcTest(ChangeMonetaryValueController.class)
class ChangeMonetaryValueControllerTest extends BaseControllerTest {

  private UUID submissionId;
  private UUID claimId;
  private MockHttpSession session;
  private String redirectUrl;

  @BeforeEach
  void setup() {
    submissionId = UUID.randomUUID();
    claimId = UUID.randomUUID();
    session = new MockHttpSession();
    redirectUrl = String.format("/submissions/%s/claims/%s/review", submissionId, claimId);
  }

  private static Stream<Arguments> validCosts() {
    return Stream.of(
        Arguments.of(PROFIT_COSTS, CrimeClaimDetails.class),
        Arguments.of(PROFIT_COSTS, CivilClaimDetails.class),
        Arguments.of(PROFIT_COSTS, MediationClaimDetails.class),
        Arguments.of(DISBURSEMENTS, CrimeClaimDetails.class),
        Arguments.of(DISBURSEMENTS, CivilClaimDetails.class),
        Arguments.of(DISBURSEMENTS, MediationClaimDetails.class),
        Arguments.of(DISBURSEMENTS_VAT, CrimeClaimDetails.class),
        Arguments.of(DISBURSEMENTS_VAT, CivilClaimDetails.class),
        Arguments.of(DISBURSEMENTS_VAT, MediationClaimDetails.class),
        Arguments.of(COUNSEL_COSTS, CivilClaimDetails.class),
        Arguments.of(COUNSEL_COSTS, MediationClaimDetails.class),
        Arguments.of(DETENTION_TRAVEL_AND_WAITING_COSTS, CivilClaimDetails.class),
        Arguments.of(DETENTION_TRAVEL_AND_WAITING_COSTS, MediationClaimDetails.class),
        Arguments.of(JR_FORM_FILLING_COSTS, CivilClaimDetails.class),
        Arguments.of(JR_FORM_FILLING_COSTS, MediationClaimDetails.class),
        Arguments.of(TRAVEL_COSTS, CrimeClaimDetails.class),
        Arguments.of(WAITING_COSTS, CrimeClaimDetails.class));
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testGetReturnsView(Cost cost, Class<?> targetClass) throws Exception {
    var claim = createClaimFor(cost, targetClass);
    var claimField = getCostClaimField(claim, cost);
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(get(buildPath(cost.getPath())).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("change-monetary-value"))
        .andExpect(model().attribute("cost", equalTo(cost)))
        .andExpect(model().attribute("form", hasProperty("value", nullValue())))
        .andExpect(model().attribute("claimFieldRow", claimField.toClaimFieldRow()));
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testGetReturns404_whenFieldIsNull(Cost cost, Class<?> targetClass) throws Exception {
    var claim = createClaimWithNullFieldFor(cost, targetClass);
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(get(buildPath(cost.getPath())).session(session))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testGetReturns404_whenFieldIsNotAssessable(Cost cost, Class<?> targetClass)
      throws Exception {
    var claim = createClaimWithUnassessableFieldFor(cost, targetClass);
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(get(buildPath(cost.getPath())).session(session))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testGetReturnsViewWhenQuestionAlreadyAnswered(Cost cost, Class<?> targetClass)
      throws Exception {
    var claim = createClaimFor(cost, targetClass);
    var claimField = ChangeMonetaryValueUtils.getCostClaimField(claim, cost);
    Assertions.assertNotNull(claimField);
    claimField.setAssessed(BigDecimal.valueOf(100));
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(get(buildPath(cost.getPath())).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("change-monetary-value"))
        .andExpect(model().attribute("cost", equalTo(cost)))
        .andExpect(model().attribute("form", hasProperty("value", is("100.00"))));
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testPostSavesValueAndRedirects(Cost cost, Class<?> targetClass) throws Exception {
    var claim = createClaimFor(cost, targetClass);
    var claimField = getCostClaimField(claim, cost);
    Assertions.assertNotNull(claimField);
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(
            post(buildPath(cost.getPath())).session(session).with(csrf()).param("value", "100"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirectUrl));

    Claim updated = (Claim) session.getAttribute(claimId.toString());

    Assertions.assertNotNull(updated);
    Assertions.assertEquals(new BigDecimal("100.00"), getCostClaimField(claim, cost).getAssessed());
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testPostReturnsBadRequestForInvalidValue(Cost cost, Class<?> targetClass) throws Exception {
    var claim = createClaimFor(cost, targetClass);
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(post(buildPath(cost.getPath())).session(session).with(csrf()).param("value", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("change-monetary-value"))
        .andExpect(content().string(containsString("must not be negative")));
  }

  @Test
  void testGetReturnsNotFoundWhenInvalidCost() throws Exception {
    mockMvc.perform(get(buildPath("foo")).session(session)).andExpect(status().isNotFound());
  }

  @Test
  void testPostReturnsNotFoundWhenInvalidCost() throws Exception {
    mockMvc
        .perform(post(buildPath("foo")).session(session).with(csrf()).param("value", "1"))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetReturnsNotFoundWhenClaimTypeMismatch() throws Exception {
    var claim = new CivilClaimDetails();
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(get(buildPath("travel-costs")).session(session))
        .andExpect(status().isNotFound());
  }

  @Test
  void testPostReturnsNotFoundWhenClaimTypeMismatch() throws Exception {
    var claim = new CivilClaimDetails();
    session.setAttribute(claimId.toString(), claim);

    mockMvc
        .perform(post(buildPath("travel-costs")).session(session).with(csrf()).param("value", "1"))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testGetRequiresRole(Cost cost) throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
    mockMvc
        .perform(get(buildPath(cost.getPath())).session(session))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("validCosts")
  void testPostRequiresRole(Cost cost) throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_CASEWORKER));
    mockMvc
        .perform(post(buildPath(cost.getPath())).session(session))
        .andExpect(status().isForbidden());
  }

  private ClaimDetails createClaimFor(Cost cost, Class<?> targetClass) {
    var claim = createClaim(cost, targetClass);
    var claimField = CostClaimField.builder().cost(cost).build();
    setCostClaimField(claim, cost, claimField);
    return claim;
  }

  private Claim createClaimWithNullFieldFor(Cost cost, Class<?> targetClass) {
    var claim = createClaim(cost, targetClass);
    setCostClaimField(claim, cost, null);
    return claim;
  }

  private Claim createClaimWithUnassessableFieldFor(Cost cost, Class<?> targetClass) {
    var claim = createClaim(cost, targetClass);
    var claimField = CostClaimField.builder().cost(cost).build();
    claimField.setAssessable(false);
    setCostClaimField(claim, cost, claimField);
    return claim;
  }

  private ClaimDetails createClaim(Cost cost, Class<?> targetClass) {
    ClaimDetails claim;
    if (CivilClaimDetails.class.equals(targetClass)) {
      claim = MockClaimsFunctions.createMockCivilClaim();
    } else if (MediationClaimDetails.class.equals(targetClass)) {
      claim = MockClaimsFunctions.createMockMediationClaim();
    } else {
      claim = MockClaimsFunctions.createMockCrimeClaim();
    }
    setCostClaimField(claim, cost, null);
    return claim;
  }

  private String buildPath(String cost) {
    return String.format("/submissions/%s/claims/%s/%s", submissionId, claimId, cost);
  }
}
