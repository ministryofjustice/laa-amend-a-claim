package uk.gov.justice.laa.amend.claim.service;

import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.PARSING_FAILURE;
import static uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus.SUCCESS;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;

@Service
public class BulkUploadService {

    public BulkUploadResult upload(MultipartFile file, UUID userId) {
        List<Object> rows;
        try {
            rows = parseFile(file);
        } catch (Exception ex) {
            return new BulkUploadResult(PARSING_FAILURE, List.of(ex.getMessage()));
        }

        var validationResult = validateRows(rows);
        if (validationResult.status() != SUCCESS) {
            return validationResult;
        }

        var submissionResult = submit(rows, userId);
        if (submissionResult.status() != SUCCESS) {
            return submissionResult;
        }

        return new BulkUploadResult(SUCCESS, List.of());
    }

    private List<Object> parseFile(MultipartFile file) {
        return List.of();
    }

    private BulkUploadResult validateRows(List<Object> rows) {
        return new BulkUploadResult(SUCCESS, List.of());
    }

    private BulkUploadResult submit(List<Object> rows, UUID userId) {
        return new BulkUploadResult(SUCCESS, List.of());
    }
}
