package uk.gov.justice.laa.amend.claim.tests;

import base.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.utils.EnvConfig;
import uk.gov.justice.laa.amend.claim.utils.TestData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

public class DbSeedSmokeTest extends BaseTest {

    @Test
    @DisplayName("DB Seed: inserts claim and verifies claim row exists")
    void seedAndVerifyClaimInsert() throws Exception {

        TestData.Scope scope = TestData.seed(
                "DbSeedSmokeTest",
                "fixtures/db/claims/insert-claim.sql"
        );

        String claimId = scope.get("claim.id");
        String submissionId = scope.get("db.claims.claim.submission_id");
        String ufn = scope.get("claim.ufn");

        Assertions.assertFalse(ufn.isBlank(), "Generated UFN must not be blank");

        String jdbcUrl = "jdbc:postgresql://"
                + EnvConfig.dbHost() + ":"
                + EnvConfig.dbPort() + "/"
                + EnvConfig.dbName();

        Map<String, String> claimRow = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(
                jdbcUrl,
                EnvConfig.dbUser(),
                EnvConfig.dbPassword()
        );
             var ps = conn.prepareStatement(
                     """
                     SELECT
                       id,
                       submission_id,
                       status,
                       schedule_reference,
                       case_reference_number,
                       unique_file_number,
                       case_start_date,
                       case_concluded_date,
                       matter_type_code,
                       fee_code,
                       procurement_area_code,
                       is_amended,
                       has_assessment,
                       version
                     FROM claims.claim
                     WHERE id = ?::uuid
                     """
             )) {

            ps.setString(1, claimId);

            try (var rs = ps.executeQuery()) {
                Assertions.assertTrue(rs.next(), "Expected claim row to exist");

                claimRow.put("id", rs.getString("id"));
                claimRow.put("submission_id", rs.getString("submission_id"));
                claimRow.put("status", rs.getString("status"));
                claimRow.put("schedule_reference", rs.getString("schedule_reference"));
                claimRow.put("case_reference_number", rs.getString("case_reference_number"));
                claimRow.put("unique_file_number", rs.getString("unique_file_number"));
                claimRow.put("case_start_date", String.valueOf(rs.getDate("case_start_date")));
                claimRow.put("case_concluded_date", String.valueOf(rs.getDate("case_concluded_date")));
                claimRow.put("matter_type_code", rs.getString("matter_type_code"));
                claimRow.put("fee_code", rs.getString("fee_code"));
                claimRow.put("procurement_area_code", rs.getString("procurement_area_code"));
                claimRow.put("is_amended", String.valueOf(rs.getBoolean("is_amended")));
                claimRow.put("has_assessment", String.valueOf(rs.getBoolean("has_assessment")));
                claimRow.put("version", String.valueOf(rs.getInt("version")));
            }
        }

        System.out.println("\n===== CLAIM ROW INSERTED =====");
        claimRow.forEach((k, v) -> System.out.printf("%-25s : %s%n", k, v));
        System.out.println("=============================\n");

        Assertions.assertEquals(claimId, claimRow.get("id"));
        Assertions.assertEquals(submissionId, claimRow.get("submission_id"));
        Assertions.assertEquals(ufn, claimRow.get("unique_file_number"));
        Assertions.assertEquals("VALID", claimRow.get("status"));
    }
}