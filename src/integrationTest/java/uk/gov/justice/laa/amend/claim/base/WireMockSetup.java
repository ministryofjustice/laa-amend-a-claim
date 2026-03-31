package uk.gov.justice.laa.amend.claim.base;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.mock.web.MockMultipartFile;

public class WireMockSetup {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setupWireMock() {
        int port = 8089;
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        WireMock.configureFor("localhost", port);
    }

    @AfterAll
    public static void stopWireMock() {
        wireMockServer.stop();
    }

    // Dynamically generate a stub for N claims with matching UFNs and office codes from the CSV file
    public static void setupGetClaimsStubDynamic(MockMultipartFile file, int n) throws Exception {

        List<String> lines = new String(file.getBytes(), UTF_8).lines().toList();

        // Extract (officeCode, ufn) pairs using streams
        List<String[]> data = lines.stream()
                .skip(1) // skip header
                .map(l -> l.split(",")) // split into columns
                .filter(cols -> cols.length >= 2) // keep only valid rows
                .map(cols -> new String[] {cols[0].trim(), cols[1].trim()})
                .toList();

        if (data.isEmpty()) {
            return;
        }

        // Use the first office code only
        String officeCode = data.getFirst()[0];

        // Build the list of claims using N UFNs
        List<Map<String, Object>> claims = new ArrayList<>();

        for (int i = 0; i < n && i < data.size(); i++) {
            String ufn = data.get(i)[1];

            Map<String, Object> claim = Map.of(
                    "id",
                    UUID.randomUUID().toString(),
                    "unique_file_number",
                    ufn,
                    "case_reference_number",
                    "REF-" + i,
                    "client_surname",
                    "Surname-" + i,
                    "date_submitted",
                    OffsetDateTime.now().minusDays(i).toString(),
                    "account",
                    "ACC" + i,
                    "type",
                    "CLAIM",
                    "office_code",
                    officeCode,
                    "status",
                    "VALID",
                    "area_of_law",
                    "LEGAL HELP");

            claims.add(claim);
        }

        String response = """
            {
                "content": %s,
                "total_elements": %d,
                "total_pages": 1,
                "number": 0,
                "size": %d
            }
            """.formatted(new ObjectMapper().writeValueAsString(claims), claims.size(), claims.size());

        // Single stub
        stubFor(get(urlPathMatching("/api/v2/claims.*"))
                .withQueryParam("office_code", equalTo(officeCode.toUpperCase()))
                .withQueryParam("page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupGetClaimsStub(String officeCode, String ufn) {
        String response = String.format("""
             {
                 "content": [
                     {
                         "unique_file_number": "%s",
                         "case_reference_number": "REF123",
                         "client_surname": "Smith",
                         "date_submitted": "%s",
                         "account": "ACC001",
                         "type": "CLAIM",
                         "office_code": "%s",
                         "status": "VALID",
                         "area_of_law": "LEGAL HELP"
                     }
                 ],
                 "total_elements": 1,
                 "total_pages": 1,
                 "number": 0,
                 "size": 1
            }\
            """, ufn, OffsetDateTime.now(), officeCode);
        stubFor(get(urlPathMatching("/api/v2/claims.*"))
                .withQueryParam("office_code", equalTo(officeCode.toUpperCase()))
                .withQueryParam("page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupGetClaimsStub() {
        String response = """
            {
                "claims": [
                    {
                        "uniqueFileNumber": "123456",
                        "caseReferenceNumber": "REF123",
                        "clientSurname": "Smith",
                        "dateSubmitted": "2024-01-01",
                        "account": "ACC001",
                        "type": "CLAIM",
                        "status": "VALID",
                        "area_of_law": "LEGAL HELP"
                    }
                ],
                "totalElements": 1,
                "totalPages": 1,
                "pageNumber": 0
            }
            """;
        stubFor(get(urlPathMatching("/api/v2/claims.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupGetEmptyClaimsStub() {
        String response = """
            {
                "claims": [],
                "totalElements": 0,
                "totalPages": 0,
                "pageNumber": 0
            }
            """;
        stubFor(get(urlPathMatching("/api/v2/claims.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupGetClaimStub(String submissionId, String claimId, String officeCode) {
        String response = String.format("""
            {
                  "id": "%s",
                  "submission_id": "%s",
                  "status": "VALID",
                  "unique_file_number": "123456",
                  "case_reference_number": "REF123",
                  "client_forename": "Jane",
                  "client_surname": "Doe",
                  "unique_client_number": "21121985/J/DOE",
                  "case_start_date": "2025-01-01",
                  "case_concluded_date": "2025-02-01",
                  "submission_period": "JAN-2025",
                  "office_code": "%s",
                  "area_of_law": "LEGAL HELP",
                  "date_submitted": "2025-01-10T14:30:00+02:00",
                  "has_assessment": false,
                  "fee_calculation_response": {
                      "fee_code": "FEE",
                      "fee_code_description": "Fee Desc",
                      "category_of_law": "Civil",
                      "bolt_on_details": {
                          "escape_case_flag": true
                      }
                  }
              }\
            """, claimId, submissionId, officeCode);

        stubFor(get(urlPathMatching(String.format("/api/v2/submissions/%s/claims/%s", submissionId, claimId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupGetProviderOfficeStub(String officeCode, String firmName) {
        String response = String.format("""
            {
                "firm": {
                    "firmName": "%s"
                }
            }\
            """, firmName);

        stubFor(get(urlPathMatching(String.format("/api/v1/provider-offices/%s", officeCode)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void setupPostAssessment202StubForAnyClaim() {
        stubFor(post(urlPathMatching("/api/v1/claims/[a-fA-F0-9\\-]+/assessments"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "message": "Assessment accepted for processing",
                                    "status": 202
                                }
                            """)));
    }
}
