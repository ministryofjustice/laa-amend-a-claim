package uk.gov.justice.laa.amend.claim.base;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;

public class WireMockSetup {

    @BeforeEach
    void setUp() {
        WireMock.reset();
        setupClaimsApiStub();
    }

    private static final String CLAIMS_RESPONSE = """
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
            }""";

    private void setupClaimsApiStub() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/api/v0/claims.*"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(CLAIMS_RESPONSE)));
    }
}
