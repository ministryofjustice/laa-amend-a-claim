package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@UserControllerAdvice.Enabled
@Controller
@RequiredArgsConstructor
@RequestMapping("/bulk-upload")
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;
    private final FeatureFlagsConfig featureFlagsConfig;

    @GetMapping()
    public String onPageLoad(HttpServletResponse response) throws IOException {
        if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return "bulk-upload";
    }

    @PostMapping()
    public String onSubmit(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute("userId") UUID userId,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response)
            throws IOException {
        if (!featureFlagsConfig.getIsBulkUploadEnabled()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        var result = bulkUploadService.upload(file, userId);

        redirectAttributes.addFlashAttribute("result", result);

        return "redirect:/bulk-upload";
    }
}
