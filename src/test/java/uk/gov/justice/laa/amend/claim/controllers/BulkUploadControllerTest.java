package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;
import static uk.gov.justice.laa.amend.claim.models.Role.ROLE_ESCAPE_CASE_BULK_UPLOADER;
import static uk.gov.justice.laa.amend.claim.models.Role.allRolesApartFrom;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.config.ThymeleafConfig;
import uk.gov.justice.laa.amend.claim.config.security.LocalSecurityConfig;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.service.MaintenanceService;

@ActiveProfiles("local")
@WebMvcTest(BulkUploadController.class)
@Import({LocalSecurityConfig.class, ThymeleafConfig.class})
public class BulkUploadControllerTest {

    private static final String PATH = "/bulk-upload";
    private static final String REDIRECT_PATH = "/bulk-upload-result";
    private static final UUID USER_ID = UUID.fromString(DummyUserSecurityService.USER_ID);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DummyUserSecurityService dummyUserSecurityService;

    @MockitoBean
    private BulkUploadService bulkUploadService;

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
    void testOnPageLoadReturnsView() throws Exception {
        mockMvc.perform(get(PATH)).andExpect(status().isOk()).andExpect(view().name("bulk-upload"));
    }

    @Test
    void testSubmitRedirectsWithResult() throws Exception {
        var file = new MockMultipartFile("file", "file.csv", "text/csv", "a,b,c\n1,2,3".getBytes());
        var result = new BulkUploadResult(SUCCESS, List.of());

        when(bulkUploadService.upload(file, USER_ID)).thenReturn(result);

        mockMvc.perform(multipart(PATH).file(file).flashAttr("userId", USER_ID).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REDIRECT_PATH))
                .andExpect(flash().attribute("result", result));
    }

    @Test
    void testSubmitReturnsErrorIfFileMissing() throws Exception {
        mockMvc.perform(multipart(PATH).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("bulk-upload"))
                .andExpect(model().attribute("fileError", "Please choose a file to upload"));
    }

    @Test
    void testSubmitReturnsErrorIfFilepathEmpty() throws Exception {
        var file = new MockMultipartFile("file", "", "text/csv", new byte[0]);
        mockMvc.perform(multipart(PATH).file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("bulk-upload"))
                .andExpect(model().attribute("fileError", "Please choose a file to upload"));
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
