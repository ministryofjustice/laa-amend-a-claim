package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleEscapeCaseBulkUploader;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;
import uk.gov.justice.laa.amend.claim.viewmodels.ThymeleafMessage;

@HasRoleEscapeCaseBulkUploader
@UserControllerAdvice.Enabled
@Controller
@RequiredArgsConstructor
@RequestMapping("/bulk-upload")
public class BulkUploadController {

  private final BulkUploadService<BulkUploadCivilClaim> bulkUploadService;
  private final FeatureFlagsConfig featureFlagsConfig;

  @GetMapping
  public String onPageLoad(HttpServletResponse response) throws IOException {
    if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }
    return "bulk-upload";
  }

  @GetMapping("/result")
  public String onPageLoad(Model model, HttpServletResponse response) throws IOException {
    if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    if (!model.containsAttribute("result")) {
      return "redirect:/bulk-upload";
    }

    return "bulk-upload-result";
  }

  @GetMapping(value = "/example", produces = "text/csv")
  public ResponseEntity<Resource> getCsvFile(HttpServletResponse response) throws IOException {
    if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    ClassPathResource csvFile = new ClassPathResource("data/example_bulk_upload.csv");

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"example_bulk_upload.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .contentLength(csvFile.contentLength())
        .body(csvFile);
  }

  @PostMapping
  public String onSubmit(
      @RequestParam(value = "file", required = false) MultipartFile file,
      @ModelAttribute("userId") UUID userId,
      RedirectAttributes redirectAttributes,
      Model model,
      HttpServletResponse response)
      throws IOException {
    if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return null;
    }

    if (file == null || Strings.isBlank(file.getOriginalFilename())) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      model.addAttribute("fileError", new ThymeleafMessage("bulkUpload.fileError.required"));
      return "bulk-upload";
    }

    var result = bulkUploadService.upload(file, userId);

    redirectAttributes.addFlashAttribute("result", result);

    return "redirect:/bulk-upload/result";
  }
}
