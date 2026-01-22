package uk.gov.justice.laa.amend.claim.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.amend.claim.base.BaseTest;
import uk.gov.justice.laa.amend.claim.models.BulkSubmissionInsert;
import uk.gov.justice.laa.amend.claim.models.CalculatedFeeDetailInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimInsert;
import uk.gov.justice.laa.amend.claim.models.ClaimSummaryFeeInsert;
import uk.gov.justice.laa.amend.claim.models.Insert;
import uk.gov.justice.laa.amend.claim.models.SubmissionInsert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class DbSeedSmokeTest extends BaseTest {

    private final String BULK_SUBMISSION_ID = UUID.randomUUID().toString();
    private final String SUBMISSION_ID = UUID.randomUUID().toString();
    private final String CLAIM_ID = UUID.randomUUID().toString();
    private final String CLAIM_SUMMARY_FEE_ID = UUID.randomUUID().toString();
    private final String CALCULATED_FEE_DETAIL_ID = UUID.randomUUID().toString();
    private final String USER_ID = UUID.randomUUID().toString();
    private final String UFN = generateUfn();

    @Override
    protected List<Insert> inserts() {
        return List.of(
            BulkSubmissionInsert
                .builder()
                .id(BULK_SUBMISSION_ID)
                .userId(USER_ID)
                .build(),

            SubmissionInsert
                .builder()
                .id(SUBMISSION_ID)
                .bulkSubmissionId(BULK_SUBMISSION_ID)
                .officeAccountNumber("123456")
                .submissionPeriod("APR-2025")
                .areaOfLaw("CRIME_LOWER")
                .userId(USER_ID)
                .build(),

            ClaimInsert
                .builder()
                .id(CLAIM_ID)
                .submissionId(SUBMISSION_ID)
                .uniqueFileNumber(UFN)
                .userId(USER_ID)
                .build(),

            ClaimSummaryFeeInsert
                .builder()
                .id(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .userId(USER_ID)
                .build(),

            CalculatedFeeDetailInsert
                .builder()
                .id(CALCULATED_FEE_DETAIL_ID)
                .claimSummaryFeeId(CLAIM_SUMMARY_FEE_ID)
                .claimId(CLAIM_ID)
                .escaped(true)
                .userId(USER_ID)
                .build()
        );
    }

    @Test
    @DisplayName("DB Seed: inserts claim and verifies claim row exists")
    void seedAndVerifyClaimInsert() throws SQLException {
        checkSeededBulkSubmissionData(SUBMISSION_ID);
        checkSeededSubmissionData(SUBMISSION_ID, BULK_SUBMISSION_ID);
        checkSeededClaimData(CLAIM_ID, SUBMISSION_ID, UFN);
        checkSeededClaimSummaryFeeData(CLAIM_SUMMARY_FEE_ID, CLAIM_ID);
        checkSeededCalculatedFeeDetailData(CALCULATED_FEE_DETAIL_ID, CLAIM_SUMMARY_FEE_ID, CLAIM_ID);
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