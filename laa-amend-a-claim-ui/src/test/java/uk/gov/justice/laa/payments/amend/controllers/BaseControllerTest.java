package uk.gov.justice.laa.payments.amend.controllers;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.payments.amend.config.FeatureFlagsConfig;
import uk.gov.justice.laa.payments.amend.config.ThymeleafConfig;
import uk.gov.justice.laa.payments.amend.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.payments.amend.models.enums.Role;
import uk.gov.justice.laa.payments.amend.service.DummyUserSecurityService;
import uk.gov.justice.laa.payments.amend.service.MaintenanceService;
import uk.gov.justice.laa.payments.amend.viewmodels.AmendmentsHeaderViewFactory;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public abstract class BaseControllerTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected DummyUserSecurityService dummyUserSecurityService;

  @MockitoBean protected FeatureFlagsConfig featureFlagsConfig;

  @MockitoBean protected MaintenanceService maintenanceService;

  @MockitoBean protected OutageBannerAdvice outageBannerAdvice;

  @MockitoBean protected AmendmentsHeaderViewFactory amendmentsHeaderViewFactory;

  @BeforeEach
  public void beforeEach() {
    dummyUserSecurityService.setRoles(Set.of(Role.values()));
  }
}
