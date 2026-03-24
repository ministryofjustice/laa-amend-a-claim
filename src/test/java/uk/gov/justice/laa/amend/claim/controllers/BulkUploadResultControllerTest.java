package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_BULK_UPLOADER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(BulkUploadResultController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class BulkUploadResultControllerTest {

    private static final String PATH = "/bulk-upload-result";
    private static final String REDIRECT_PATH = "/bulk-upload";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    private FeatureFlagsConfig featureFlagsConfig;

    @MockitoBean
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setup() {
        when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);

        dummyUserSecurityService.setRoles(Set.of(ROLE_ESCAPE_CASE_BULK_UPLOADER));
    }

    @Test
    void testOnPageLoadReturnsViewIfResultSet() throws Exception {
        var result = new BulkUploadResult(SUCCESS, List.of("all is good"));
        mockMvc.perform(get(PATH).flashAttr("result", result))
                .andExpect(status().isOk())
                .andExpect(view().name("bulk-upload-result"));
    }

    @Test
    void testOnPageLoadRedirectsIfResultUnset() throws Exception {
        mockMvc.perform(get(PATH)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(REDIRECT_PATH));
    }

    @Test
    void testGetRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
        mockMvc.perform(get(PATH)).andExpect(status().isForbidden());
    }

    @Test
    void testPostRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
        mockMvc.perform(post(PATH)).andExpect(status().isForbidden());
    }
}
