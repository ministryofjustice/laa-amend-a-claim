package uk.gov.justice.laa.amend.claim.bulkupload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
class BulkUploadServiceIntegrationTest extends WireMockSetup {

  private static final String[] HEADERS = {
    "Office Code",
    "UFN",
    "Assessment Outcome",
    "Profit Cost",
    "Disbursements",
    "Disbursements VAT",
    "Counsel Costs",
    "Total Allowed VAT",
    "Total Allowed Including VAT"
  };

  @Autowired private BulkUploadService<BulkUploadCivilClaim> bulkUploadService;

  @Test
  @DisplayName("Parses 130 CSV rows end-to-end successfully")
  void parseCsvRowsSuccessfully() throws Exception {
    int rows = 130;
    MockMultipartFile file = csvFileWithRows(rows);
    UUID userId = UUID.randomUUID();

    // Dynamically stub claims with matching UFNs and office codes
    WireMockSetup.setupGetClaimsStubDynamic(file, rows);
    WireMockSetup.setupPostAssessment202StubForAnyClaim();
    BulkUploadResult result = bulkUploadService.upload(file, userId);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(BulkUploadStatus.SUCCESS);
  }

  /** Builds a CSV file with valid headers and N rows. */
  private MockMultipartFile csvFileWithRows(int n) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVPrinter csv =
            new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {

      Random random = new Random();
      String officeCode = generateRandomOfficeCode(random);

      List<String> ufns =
          Stream.generate(() -> generateRandomUfn(random)).distinct().limit(n).toList();

      IntStream.range(0, n)
          .forEach(
              i -> {
                String ufn = ufns.get(i);

                String assessment = (i % 2 == 0) ? "Reduced" : "Reduced To Fixed Fee";

                String profitCost = "£" + getNumber(i * 10 + 100) + ".00";
                String disbursements = "£" + getNumber(i * 5 + 50) + ".00";
                String disbVat = "£" + getNumber(i + 5) + ".20";
                String counsel = "£" + getNumber(i * 2 + 10) + ".55";
                String totalAllowedVat = "£" + getNumber(i * 3 + 15) + ".75";
                String totalAllowedInclVat = "£" + getNumber(i * 21 + 175) + ".95";

                try {
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
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
    }

    return new MockMultipartFile(
        "file", "bulk-civil-claims.csv", "text/csv", new ByteArrayInputStream(baos.toByteArray()));
  }

  private String generateRandomOfficeCode(Random random) {
    String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder sb = new StringBuilder("0P");
    for (int i = 0; i < 4; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  /** Generates UFN in format DDMMYY/XYZ (randomized for tests) */
  private String generateRandomUfn(Random random) {
    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
    int randomBlock = random.nextInt(900) + 100; // 100–999
    return date + "/" + randomBlock;
  }

  private String getNumber(int value) {
    return String.format("%,d", value);
  }

  @Test
  @DisplayName("Returns error when ClaimService returns null (no claims found)")
  void returnsErrorWhenClaimServiceReturnsNull() throws Exception {
    // Create a CSV with a UFN and office code
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVPrinter csv =
            new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(HEADERS).get())) {
      csv.printRecord(
          "0p0001", // office code
          "010101/001", // UFN
          "Reduced",
          "£100.00",
          "£50.00",
          "£5.20",
          "£10.55",
          "£15.75",
          "£175.95");
    }
    WireMockSetup.setupGetEmptyClaimsStub();
    MockMultipartFile file =
        new MockMultipartFile(
            "file",
            "bulk-civil-claims.csv",
            "text/csv",
            new ByteArrayInputStream(baos.toByteArray()));
    UUID userId = UUID.randomUUID();

    BulkUploadResult result = bulkUploadService.upload(file, userId);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(BulkUploadStatus.VALIDATION_FAILURE);
    assertThat(result.errors())
        .anyMatch(
            error ->
                error
                    .message()
                    .contains(
                        "Escaped Civil Claim not found for UFN 010101/001 and officeCode 0P0001"));
  }
}
