package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@SpringBootTest
class BulkUploadServiceParsingErrorTest {

    @Autowired
    private BulkUploadService<?> bulkUploadService;

    @Test
    @DisplayName("Returns parsing errors for header in result")
    void returnParsingErrorsWhenHeaderInvalid() throws Exception {
        // CSV with missing required header and a bad row
        String csv = "UFN,Assessment Outcome,Profit Cost\n 0p322f,Reduced,notanumber\n 0A456H,Reduced,100.00\n";
        MockMultipartFile file =
                new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        BulkUploadResult result = bulkUploadService.upload(file, userId);
        assertThat(result.status()).isEqualTo(BulkUploadStatus.PARSING_FAILURE);
        List<String> errors = result.reasons();
        assertThat(errors).isNotEmpty();
        // Should contain header error
        assertThat(errors.stream().anyMatch(e -> e.toLowerCase().contains("header")))
                .isTrue();
    }

    @Test
    @DisplayName("Returns all parsing errors for rows in result")
    void returnsAllParsingErrors() {

        String invalidCsv =
                "Office Code,UFN,Assessment Outcome,Profit Cost,Disbursements,Disbursements VAT,Counsel costs,Total"
                        + " allowed vat,Total allowed include vat\n"
                        + "0p1ab3,120223/001101,Reduced,12.65,50.00,\"£10,245\",88,100,1,234.56\n"
                        + "0p9zq7,120223/001274,Reduced To Fixed Fee,ten,£33.20,12.5.7,£0.00,--44,### \n"
                        + "0p5k2m,120223/001593,Reduced,1,2,3,£12.00,£7.50,12.12.12,£9.99\n"
                        + "0p7bn1,120223/001846,Reduced To Fixed Fee, ,£20.00,£1.20,£A1.00,£3.50,£5,00\n"
                        + "0p3t8x,120223/001905,Reduced,£-,-12.00,£0.20,£12.34.56,£**2**,£99..10\n"
                        + "0pc4r6,120223/001327,Reduced To Fixed Fee,£££100,£15.00,one,£5.55,£6.75,£7.95GBP\n"
                        + "0pqu9e,120223/001712,Reduced,NaN,£0.00,£3,33,£10.10,£2O.00,£1.00\n"
                        + "0p0jk2,120223/001468,Reduced To Fixed Fee,∞,£-10.00,£0.00,£**10**,£5.5.5,£12,34\n"
                        + "0pab7q,120223/001689,Reduced,£12,34,£5.00,£0.2O,£x.99,£3.14,£7..77\n"
                        + "0pmm4d,120223/001753,Reduced To Fixed Fee,£,£,£,£,£,£";

        MockMultipartFile file =
                new MockMultipartFile("file", "test.csv", "text/csv", invalidCsv.getBytes(StandardCharsets.UTF_8));
        UUID userId = UUID.randomUUID();
        BulkUploadResult result = bulkUploadService.upload(file, userId);
        assertThat(result.status()).isEqualTo(BulkUploadStatus.PARSING_FAILURE);
        List<String> errors = result.reasons();
        assertThat(errors).isNotEmpty();
        assertThat(errors.size()).isEqualTo(9);
        // Should contain row errors
        assertThat(errors.stream().anyMatch(e -> e.toLowerCase().contains("row")))
                .isTrue();
    }
}
