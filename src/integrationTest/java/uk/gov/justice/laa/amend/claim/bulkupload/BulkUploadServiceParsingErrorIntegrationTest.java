package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import uk.gov.justice.laa.amend.claim.base.WireMockSetup;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@SpringBootTest
class BulkUploadServiceParsingErrorIntegrationTest extends WireMockSetup {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private BulkUploadService<BulkUploadCivilClaim> bulkUploadService;

    @Test
    @DisplayName("Returns parsing errors for header in result")
    void returnParsingErrorsWhenHeaderInvalid() throws Exception {
        // CSV with missing required header and a bad row
        String csv = """
            UFN,Assessment Outcome,Profit Cost
            0p322f,Reduced,notanumber
            0A456H,Reduced,100.00
            """;

        MockMultipartFile file =
                new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        BulkUploadResult result = bulkUploadService.upload(file, USER_ID);

        assertThat(result.status()).isEqualTo(BulkUploadStatus.PARSING_FAILURE);
        assertThat(result.errors())
                .containsExactly(new BulkUploadError(
                        null,
                        "Missing required headers: Office Code, Disbursements, Disbursements VAT, Counsel"
                                + " Costs, Total Allowed VAT, Total Allowed Including VAT"));
    }

    @Test
    @DisplayName("Returns all parsing errors for rows in result")
    void returnsAllParsingErrors() {

        String invalidCsv = """
            Office Code,UFN,Assessment Outcome,Profit Cost,Disbursements,Disbursements VAT,Counsel Costs,Total Allowed VAT,Total Allowed Including VAT
            0p1ab3,120223/001101,Reduced,12.65,50.00,"£10,245",88,100,1,234.56
            0p9zq7,120223/001274,Reduced To Fixed Fee,ten,£33.20,12.5.7,£0.00,--44,###
            0p5k2m,120223/001593,Reduced,1,2,3,£12.00,£7.50,12.12.12,£9.99
            0p7bn1,120223/001846,Reduced To Fixed Fee, ,£20.00,£1.20,£A1.00,£3.50,£5,00
            0p3t8x,120223/001905,Reduced,£-,-12.00,£0.20,£12.34.56,£**2**,£99..10
            0pc4r6,120223/001327,Reduced To Fixed Fee,£££100,£15.00,one,£5.55,£6.75,£7.95GBP
            0pqu9e,120223/001712,Reduced,NaN,£0.00,£3,33,£10.10,£2O.00,£1.00
            0p0jk2,120223/001468,Reduced To Fixed Fee,∞,£-10.00,£0.00,£**10**,£5.5.5,£12,34
            0pab7q,120223/001689,Reduced,£12,34,£5.00,£0.2O,£x.99,£3.14,£7..77
            0pmm4d,120223/001753,Reduced To Fixed Fee,£,£,£,£,£,£
            """;

        MockMultipartFile file =
                new MockMultipartFile("file", "test.csv", "text/csv", invalidCsv.getBytes(StandardCharsets.UTF_8));

        BulkUploadResult result = bulkUploadService.upload(file, USER_ID);

        assertThat(result.status()).isEqualTo(BulkUploadStatus.PARSING_FAILURE);
        assertThat(result.errors())
                .containsExactly(
                        new BulkUploadError(3, "Invalid number in Profit Cost"),
                        new BulkUploadError(3, "Invalid number in Disbursements VAT"),
                        new BulkUploadError(3, "Invalid number in Total Allowed VAT"),
                        new BulkUploadError(3, "Invalid number in Total Allowed Including VAT"),
                        new BulkUploadError(4, "Invalid number in Total Allowed Including VAT"),
                        new BulkUploadError(5, "Profit Cost is required"),
                        new BulkUploadError(5, "Invalid number in Counsel Costs"),
                        new BulkUploadError(6, "Invalid number in Profit Cost"),
                        new BulkUploadError(6, "Invalid number in Counsel Costs"),
                        new BulkUploadError(6, "Invalid number in Total Allowed VAT"),
                        new BulkUploadError(6, "Invalid number in Total Allowed Including VAT"),
                        new BulkUploadError(7, "Invalid number in Disbursements VAT"),
                        new BulkUploadError(7, "Invalid number in Total Allowed Including VAT"),
                        new BulkUploadError(8, "Invalid number in Profit Cost"),
                        new BulkUploadError(8, "Invalid number in Total Allowed Including VAT"),
                        new BulkUploadError(9, "Invalid number in Profit Cost"),
                        new BulkUploadError(9, "Invalid number in Counsel Costs"),
                        new BulkUploadError(9, "Invalid number in Total Allowed VAT"),
                        new BulkUploadError(10, "Invalid number in Counsel Costs"),
                        new BulkUploadError(10, "Invalid number in Total Allowed VAT"),
                        new BulkUploadError(11, "Invalid number in Profit Cost"),
                        new BulkUploadError(11, "Invalid number in Disbursements"),
                        new BulkUploadError(11, "Invalid number in Disbursements VAT"),
                        new BulkUploadError(11, "Invalid number in Counsel Costs"),
                        new BulkUploadError(11, "Invalid number in Total Allowed VAT"),
                        new BulkUploadError(11, "Invalid number in Total Allowed Including VAT"));
    }

    @Test
    @DisplayName("Returns all validation errors for rows in result")
    void returnsAllValidationErrors() {

        String invalidCsv = """
            Office Code,UFN,Assessment Outcome,Profit Cost,Disbursements,Disbursements VAT,Counsel Costs,Total Allowed VAT,Total Allowed Including VAT
            123456,012345/012,Reduced,12.65,50.00,10.25,88.00,100.00,1234.56
            123456,Badly formatted UFN,Reduced To Fixed Fee,10.00,33.20,12.57,0.00,66.00,144.32
            123456,012345/012,Reduced,12.65,50.00,10.25,88.00,100.00,1234.56
            """; // Final row is duplicate row of the first (same officeCode + UFN)

        MockMultipartFile file =
                new MockMultipartFile("file", "test.csv", "text/csv", invalidCsv.getBytes(StandardCharsets.UTF_8));
        setupGetClaimsStub();

        BulkUploadResult result = bulkUploadService.upload(file, USER_ID);

        assertThat(result.status()).isEqualTo(BulkUploadStatus.VALIDATION_FAILURE);
        assertThat(result.errors())
                .containsExactly(
                        new BulkUploadError(
                                2, "Escaped Civil Claim not found for UFN 012345/012 and officeCode 123456"),
                        new BulkUploadError(3, "Invalid UFN Badly formatted UFN"),
                        new BulkUploadError(
                                3, "Escaped Civil Claim not found for UFN Badly formatted UFN and officeCode 123456"),
                        new BulkUploadError(4, "Duplicate row for office code 123456 and UFN 012345/012"),
                        new BulkUploadError(
                                4, "Escaped Civil Claim not found for UFN 012345/012 and officeCode 123456"));
    }
}
