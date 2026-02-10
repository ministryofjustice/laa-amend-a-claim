package uk.gov.justice.laa.amend.claim.base;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class WireMockSetup {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setupWireMock() {
        int port = 8089;
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        WireMock.configureFor("localhost", port);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    public void setupGetClaimsStub() {
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
                        "status": "PENDING"
                    }
                ],
                "totalElements": 1,
                "totalPages": 1,
                "pageNumber": 0
            }
            """;
        stubFor(get(urlPathMatching("/api/v1/claims.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public void setupGetClaimStub(String submissionId, String claimId) {
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
            """, claimId, submissionId);

        stubFor(get(urlPathMatching(String.format("/api/v1/submissions/%s/claims/%s", submissionId, claimId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public void setupGetSubmissionStub(String submissionId, String officeAccountNumber) {
        String response = String.format("""
            {
                "id": "%s",
                "office_account_number": "%s",
                "area_of_law": "LEGAL HELP",
                "submitted": "2025-01-10T14:30:00+02:00"
            }\
            """, submissionId, officeAccountNumber);

        stubFor(get(urlPathMatching(String.format("/api/v1/submissions/%s", submissionId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public void setupGetProviderOfficeStub(String officeAccountNumber, String firmName) {
        String response = String.format("""
            {
                "firm": {
                    "firmName": "%s"
                }
            }\
            """, firmName);

        stubFor(get(urlPathMatching(String.format("/api/v1/provider-offices/%s", officeAccountNumber)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }
}
