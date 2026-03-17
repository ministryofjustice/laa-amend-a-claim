package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import uk.gov.justice.laa.amend.claim.bulkupload.civil.BulkUploadCivilClaim;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult;
import uk.gov.justice.laa.amend.claim.models.BulkUploadResult.BulkUploadStatus;
import uk.gov.justice.laa.amend.claim.service.BulkUploadService;

@SpringBootTest
class BulkUploadServiceTest {

    private static final String[] HEADERS = {
        "Office Code",
        "UFN",
        "Assessment Outcome",
        "Profit Cost",
        "Disbursements",
        "Disbursements VAT",
        "Counsel costs",
        "Total allowed vat",
        "Total allowed include vat"
    };

    @Autowired
    private BulkUploadService<BulkUploadCivilClaim> bulkUploadService;

    @Test
    @DisplayName("Parses 4,000 CSV rows end-to-end successfully")
    void parsesThousandRows() throws Exception {

        int rows = 4000;
        MockMultipartFile file = csvFileWithRows(rows);
        UUID userId = UUID.randomUUID();

        long start = System.nanoTime();
        BulkUploadResult result = bulkUploadService.upload(file, userId);
        long duration = (System.nanoTime() - start) / 1000000;

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(BulkUploadStatus.SUCCESS);

        System.out.println("Parsed " + rows + " rows in " + duration + " ms");
    }

    /**
     * Builds a CSV file with valid headers and N rows.
     */
    private MockMultipartFile csvFileWithRows(int n) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
                CSVPrinter csv = new CSVPrinter(
                        writer, CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {

            Random random = new Random();

            for (int i = 0; i < n; i++) {

                String officeCode = generateRandomOfficeCode(random);
                String ufn = generateRandomUfn(random);
                String assessment = (i % 2 == 0) ? "Reduced" : "Reduced To Fixed Fee";

                String profitCost = "£" + getNumber(i * 10 + 100) + ".00";
                String disbursements = "£" + getNumber(i * 5 + 50) + ".00";
                String disbVat = "£" + getNumber(i + 5) + ".20";
                String counsel = "£" + getNumber(i * 2 + 10) + ".55";
                String totalAllowedVat = "£" + getNumber(i * 3 + 15) + ".75";
                String totalAllowedInclVat = "£" + getNumber(i * 21 + 175) + ".95";

                csv.printRecord(
                        officeCode,
                        ufn,
                        assessment,
                        profitCost,
                        disbursements,
                        disbVat,
                        counsel,
                        totalAllowedVat,
                        totalAllowedInclVat);
            }
        }

        return new MockMultipartFile(
                "file", "bulk-civil-claims.csv", "text/csv", new ByteArrayInputStream(baos.toByteArray()));
    }

    private String generateRandomOfficeCode(Random random) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder("0p");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates UFN in format DDMMYY/001XYZ (randomised for tests)
     */
    private String generateRandomUfn(Random random) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        int randomBlock = random.nextInt(900) + 100; // 100–999
        return date + "/001" + randomBlock;
    }

    private String getNumber(int value) {
        return String.format("%,d", value);
    }
}
