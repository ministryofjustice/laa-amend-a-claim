package uk.gov.justice.laa.amend.claim.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
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
  private static final String EXAMPLE_PATH = "/bulk-upload/example";
  private static final UUID USER_ID = UUID.fromString(DummyUserSecurityService.USER_ID);

  @MockitoBean private BulkUploadService bulkUploadService;

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
    var result = BulkUploadResult.success(List.of());

    when(bulkUploadService.upload(file, USER_ID)).thenReturn(result);

    mockMvc
        .perform(multipart(PATH).file(file).flashAttr("userId", USER_ID).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(RESULT_PATH))
        .andExpect(flash().attribute("result", result));
  }

  @Test
  void testSubmitReturnsErrorIfFileMissing() throws Exception {
    mockMvc
        .perform(multipart(PATH).with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("bulk-upload"))
        .andExpect(
            model().attribute("fileError", new ThymeleafMessage("bulkUpload.fileError.required")));
  }

  @Test
  void testSubmitReturnsErrorIfFilepathEmpty() throws Exception {
    var file = new MockMultipartFile("file", "", "text/csv", new byte[0]);
    mockMvc
        .perform(multipart(PATH).file(file).with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(view().name("bulk-upload"))
        .andExpect(
            model().attribute("fileError", new ThymeleafMessage("bulkUpload.fileError.required")));
  }

  @Test
  void testResultOnPageLoadReturnsViewIfResultSet() throws Exception {
    var result = BulkUploadResult.success(List.of());
    mockMvc
        .perform(get(RESULT_PATH).flashAttr("result", result))
        .andExpect(status().isOk())
        .andExpect(view().name("bulk-upload-result"));
  }

  @Test
  void testResultOnPageLoadRedirectsIfResultUnset() throws Exception {
    mockMvc
        .perform(get(RESULT_PATH))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(PATH));
  }

  @Test
  void testExampleCsvDownload() throws Exception {
    mockMvc
        .perform(get(EXAMPLE_PATH))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(CONTENT_DISPOSITION, "attachment; filename=\"example_bulk_upload.csv\""))
        .andExpect(content().contentType("text/csv"))
        .andExpect(
            content()
                .string(
                    """
                    Office Code,UFN,Assessment Outcome,Profit Cost,Disbursements,Disbursements VAT,Counsel Costs,Total Allowed VAT,Total Allowed Including VAT
                    1A123B,123456/001,Paid in Full,101.00,11.00,2.20,30.00,26.20,170.40
                    1A123B,123456/002,Reduced,101.00,11.00,2.20,30.00,26.20,170.40
                    1A123B,123456/003,Reduced to Fixed Fee,101.00,11.00,2.20,30.00,26.20,170.40
                    1A123B,123456/004,Reduced to Fixed Fee - Assessed,101.00,11.00,2.20,30.00,26.20,170.40
                    1A123B,123456/005,Nilled,101.00,11.00,2.20,30.00,26.20,170.40
                    """));
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
  void testExampleGetRequiresRole() throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
    mockMvc.perform(get(EXAMPLE_PATH)).andExpect(status().isForbidden());
  }

  @Test
  void testPostRequiresRole() throws Exception {
    dummyUserSecurityService.setRoles(allRolesApartFrom(ROLE_ESCAPE_CASE_BULK_UPLOADER));
    mockMvc.perform(post(PATH)).andExpect(status().isForbidden());
  }
}
