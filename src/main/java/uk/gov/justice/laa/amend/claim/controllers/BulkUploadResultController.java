package uk.gov.justice.laa.amend.claim.controllers;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.justice.laa.amend.claim.annotations.HasRoleEscapeCaseBulkUploader;
import uk.gov.justice.laa.amend.claim.config.FeatureFlagsConfig;

@HasRoleEscapeCaseBulkUploader
@UserControllerAdvice.Enabled
@Controller
@RequiredArgsConstructor
@RequestMapping("/bulk-upload-result")
public class BulkUploadResultController {

    private final FeatureFlagsConfig featureFlagsConfig;

    @GetMapping()
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
}
