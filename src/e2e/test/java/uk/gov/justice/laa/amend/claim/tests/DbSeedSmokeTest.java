package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

public class DbSeedSmokeTest extends BaseTest {

    @Test
    @DisplayName("DB Seed: inserts claim and verifies claim row exists")
    void seedAndVerifyClaimInsert() throws SQLException {

        String calculatedFeeDetailId = scope.get("calculated_fee_detail.id");
        String claimSummaryFeeId = scope.get("claim_summary_fee.id");
        String claimId = scope.get("claim.id");
        String submissionId = scope.get("submission.id");
        String ufn = scope.get("claim.ufn");

        Assertions.assertFalse(ufn.isBlank(), "Generated UFN must not be blank");

        checkSeededClaimData(claimId, submissionId, ufn);
        checkSeededClaimSummaryFeeData(claimSummaryFeeId, claimId);
        checkSeededCalculatedFeeDetailData(calculatedFeeDetailId, claimSummaryFeeId, claimId);
    }

    private void checkSeededClaimData(String claimId, String submissionId, String ufn) throws SQLException {
        String query = """
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
            """;
        Map<Integer, Object> parameters = Map.of(1, claimId);
        ResultSet rs = dqe.executeQuery(query, parameters);

        printResult(rs, "claim");

        Assertions.assertEquals(claimId, rs.getString("id"));
        Assertions.assertEquals(submissionId, rs.getString("submission_id"));
        Assertions.assertEquals(ufn, rs.getString("unique_file_number"));
        Assertions.assertEquals("VALID", rs.getString("status"));
    }

    private void checkSeededClaimSummaryFeeData(String claimSummaryFeeId, String claimId) throws SQLException {
        String query = """
            SELECT
              id,
              claim_id
            FROM claims.claim_summary_fee
            WHERE id = ?::uuid
            """;
        Map<Integer, Object> parameters = Map.of(1, claimSummaryFeeId);
        ResultSet rs = dqe.executeQuery(query, parameters);

        printResult(rs, "claim_summary_fee");

        Assertions.assertEquals(claimSummaryFeeId, rs.getString("id"));
        Assertions.assertEquals(claimId, rs.getString("claim_id"));
    }

    private void checkSeededCalculatedFeeDetailData(String calculatedFeeDetailId, String claimSummaryFeeId, String claimId) throws SQLException {
        String query = """
            SELECT
              id,
              claim_summary_fee_id,
              claim_id
            FROM claims.calculated_fee_detail
            WHERE id = ?::uuid
            """;
        Map<Integer, Object> parameters = Map.of(1, calculatedFeeDetailId);
        ResultSet rs = dqe.executeQuery(query, parameters);

        printResult(rs, "calculated_fee_detail");

        Assertions.assertEquals(calculatedFeeDetailId, rs.getString("id"));
        Assertions.assertEquals(claimSummaryFeeId, rs.getString("claim_summary_fee_id"));
        Assertions.assertEquals(claimId, rs.getString("claim_id"));
    }

    private void printResult(ResultSet rs, String table) throws SQLException {
        Assertions.assertTrue(rs.next(), "Expected claim row to exist");
        System.out.printf("\n===== ROW INSERTED into %s =====%n", table);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnLabel(i); // respects AS aliases
            Object value = rs.getObject(i);
            System.out.printf("%-25s : %s%n", columnName, value);
        }
        System.out.println("=============================\n");
    }
}