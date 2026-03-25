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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;
import uk.gov.justice.laa.amend.claim.service.DummyUserSecurityService;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;

@WebMvcTest(BulkUploadController.class)
public class BulkUploadControllerTest extends BaseControllerTest {

    private static final String PATH = "/bulk-upload";
    private static final String RESULT_PATH = "/bulk-upload/result";
    private static final UUID USER_ID = UUID.fromString(DummyUserSecurityService.USER_ID);

    @MockitoBean
    private BulkUploadService bulkUploadService;

    @BeforeEach
    void setup() {
        when(featureFlagsConfig.getIsBulkUploadEnabled()).thenReturn(true);
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
                .andExpect(redirectedUrl(RESULT_PATH))
                .andExpect(flash().attribute("result", result));
    }

    @Test
    void testSubmitReturnsErrorIfFileMissing() throws Exception {
        mockMvc.perform(multipart(PATH).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("bulk-upload"))
                .andExpect(model().attribute("fileError", new ThymeleafMessage("bulkUpload.fileError.required")));
    }

    @Test
    void testSubmitReturnsErrorIfFilepathEmpty() throws Exception {
        var file = new MockMultipartFile("file", "", "text/csv", new byte[0]);
        mockMvc.perform(multipart(PATH).file(file).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("bulk-upload"))
                .andExpect(model().attribute("fileError", new ThymeleafMessage("bulkUpload.fileError.required")));
    }

    @Test
    void testResultOnPageLoadReturnsViewIfResultSet() throws Exception {
        var result = new BulkUploadResult(SUCCESS, List.of("all is good"));
        mockMvc.perform(get(RESULT_PATH).flashAttr("result", result))
                .andExpect(status().isOk())
                .andExpect(view().name("bulk-upload-result"));
    }

    @Test
    void testResultOnPageLoadRedirectsIfResultUnset() throws Exception {
        mockMvc.perform(get(RESULT_PATH)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(PATH));
    }

    @Test
    void testGetRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
        mockMvc.perform(get(PATH)).andExpect(status().isForbidden());
    }

    @Test
    void testResultGetRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
        mockMvc.perform(get(RESULT_PATH)).andExpect(status().isForbidden());
    }

    @Test
    void testPostRequiresRole() throws Exception {
        dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
        mockMvc.perform(post(PATH)).andExpect(status().isForbidden());
    }
}
