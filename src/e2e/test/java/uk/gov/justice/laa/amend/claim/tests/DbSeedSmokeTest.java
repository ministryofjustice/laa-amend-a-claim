package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DbSeedSmokeTest extends BaseTest {

    @Test
    @DisplayName("DB Seed: inserts claim and verifies claim row exists")
    void seedAndVerifyClaimInsert() throws SQLException {
        String calculatedFeeDetailId = dqe.getCalculatedFeeDetailId();
        String claimSummaryFeeId = dqe.getClaimSummaryFeeId();
        String claimId = dqe.getClaimId();
        String submissionId = dqe.getSubmissionId();
        String bulkSubmissionId = dqe.getBulkSubmissionId();
        String ufn = dqe.getUfn();

        Assertions.assertFalse(ufn.isBlank(), "Generated UFN must not be blank");

        checkSeededBulkSubmissionData(bulkSubmissionId);
        checkSeededSubmissionData(submissionId, bulkSubmissionId);
        checkSeededClaimData(claimId, submissionId, ufn);
        checkSeededClaimSummaryFeeData(claimSummaryFeeId, claimId);
        checkSeededCalculatedFeeDetailData(calculatedFeeDetailId, claimSummaryFeeId, claimId);
    }

    private void checkSeededBulkSubmissionData(String bulkSubmissionId) throws SQLException {
        final String table = "bulk_submission";
        try (ResultSet rs = dqe.select(table, bulkSubmissionId)) {
            while (rs.next()) {
                printResult(rs, table);

                Assertions.assertEquals(bulkSubmissionId, rs.getString("id"));
            }
        }
    }

    private void checkSeededSubmissionData(String submissionId, String bulkSubmissionId) throws SQLException {
        final String table = "submission";
        try (ResultSet rs = dqe.select(table, submissionId)) {
            while (rs.next()) {
                printResult(rs, table);

                Assertions.assertEquals(submissionId, rs.getString("id"));
                Assertions.assertEquals(bulkSubmissionId, rs.getString("bulk_submission_id"));
                Assertions.assertEquals("123456", rs.getString("office_account_number"));
                Assertions.assertEquals("MAR-2020", rs.getString("submission_period"));
                Assertions.assertEquals("LEGAL_HELP", rs.getString("area_of_law"));
            }
        }
    }

    private void checkSeededClaimData(String claimId, String submissionId, String ufn) throws SQLException {
        final String table = "claim";
        try (ResultSet rs = dqe.select(table, claimId)) {
            while (rs.next()) {
                printResult(rs, table);

                Assertions.assertEquals(claimId, rs.getString("id"));
                Assertions.assertEquals(submissionId, rs.getString("submission_id"));
                Assertions.assertEquals(ufn, rs.getString("unique_file_number"));
                Assertions.assertEquals("VALID", rs.getString("status"));
            }
        }
    }

    private void checkSeededClaimSummaryFeeData(String claimSummaryFeeId, String claimId) throws SQLException {
        final String table = "claim_summary_fee";
        try (ResultSet rs = dqe.select(table, claimSummaryFeeId)) {
            while (rs.next()) {
                printResult(rs, table);

                Assertions.assertEquals(claimSummaryFeeId, rs.getString("id"));
                Assertions.assertEquals(claimId, rs.getString("claim_id"));
            }
        }
    }

    private void checkSeededCalculatedFeeDetailData(String calculatedFeeDetailId, String claimSummaryFeeId, String claimId) throws SQLException {
        final String table = "calculated_fee_detail";
        try (ResultSet rs = dqe.select(table, calculatedFeeDetailId)) {
            while (rs.next()) {
                printResult(rs, table);

                Assertions.assertEquals(calculatedFeeDetailId, rs.getString("id"));
                Assertions.assertEquals(claimSummaryFeeId, rs.getString("claim_summary_fee_id"));
                Assertions.assertEquals(claimId, rs.getString("claim_id"));
            }
        }
    }

    private void printResult(ResultSet rs, String table) throws SQLException {
        System.out.printf("\n===== ROW INSERTED into %s =====%n", table);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnLabel(i);
            Object value = rs.getObject(i);
            System.out.printf("%-25s : %s%n", columnName, value);
        }
        System.out.println("=============================\n");
    }
}