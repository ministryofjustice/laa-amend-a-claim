package uk.gov.justice.laa.amend.claim.controllers;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.AppProperties;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.Role;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(ConfirmationController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class, AppProperties.class})
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    protected FeatureFlagsConfig featureFlagsConfig;

    @MockitoBean
    protected MaintenanceService maintenanceService;

    @BeforeEach
    public void beforeEach() {
        dummyUserSecurityService.setRoles(Set.of(Role.values()));
    }
}
