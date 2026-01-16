package uk.gov.justice.laa.amend.claim.persistence;

import uk.gov.justice.laa.amend.claim.config.EnvConfig;
import uk.gov.justice.laa.amend.claim.models.SqlStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.justice.laa.amend.claim.utils.TestDataUtils.generateUfn;

public class DatabaseQueryExecutor implements AutoCloseable {

    private final Connection connection;

    private final String calculatedFeeDetailId;
    private final String claimSummaryFeeId;
    private final String claimId;
    private final String submissionId;
    private final String bulkSubmissionId;
    private final String ufn;
    private final String createdBy;

    public DatabaseQueryExecutor() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", EnvConfig.dbHost(), EnvConfig.dbPort(), EnvConfig.dbName());
        this.connection = DriverManager.getConnection(url, EnvConfig.dbUser(), EnvConfig.dbPassword());
        this.calculatedFeeDetailId = UUID.randomUUID().toString();
        this.claimSummaryFeeId = UUID.randomUUID().toString();
        this.claimId = UUID.randomUUID().toString();
        this.submissionId = UUID.randomUUID().toString();
        this.bulkSubmissionId = UUID.randomUUID().toString();
        this.ufn = generateUfn();
        this.createdBy = EnvConfig.getOrDefault("DB_CREATED_BY_USER_ID", "Data-Claims-Event-Service");
    }

    public String getCalculatedFeeDetailId() {
        return calculatedFeeDetailId;
    }

    public String getClaimSummaryFeeId() {
        return claimSummaryFeeId;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getBulkSubmissionId() {
        return bulkSubmissionId;
    }

    public String getUfn() {
        return ufn;
    }

    public ResultSet executeQuery(SqlStatement sql) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.sql());

            for (Map.Entry<Integer, Object> entry : sql.getParameters().entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            return preparedStatement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL fixture against DB", e);
        }
    }

    public void executeUpdate(SqlStatement sql) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.sql());

            for (Map.Entry<Integer, Object> entry : sql.getParameters().entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL fixture against DB", e);
        }
    }

    public void seed() {
        List<SqlStatement> files = List.of(
            SqlStatement.fromFile("insert-bulk-submission", List.of(bulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("insert-submission", List.of(submissionId, bulkSubmissionId, createdBy, createdBy)),
            SqlStatement.fromFile("insert-claim", List.of(claimId, submissionId, ufn, createdBy, createdBy)),
            SqlStatement.fromFile("insert-claim-summary-fee", List.of(claimSummaryFeeId, claimId, createdBy)),
            SqlStatement.fromFile("insert-calculated-fee-detail", List.of(calculatedFeeDetailId, claimSummaryFeeId, claimId, createdBy))
        );

        files.forEach(this::executeUpdate);
    }

    public void delete() {
        delete("calculated_fee_detail", calculatedFeeDetailId);
        delete("claim_summary_fee", claimSummaryFeeId);
        delete("claim", claimId);
        delete("submission", submissionId);
        delete("bulk_submission", bulkSubmissionId);
    }

    private void delete(String table, String id) {
        String sql = String.format("DELETE FROM claims.%s WHERE id = ?::uuid", table);
        executeUpdate(SqlStatement.fromRaw(sql, List.of(id)));
    }

    public ResultSet select(String table, String id) {
        String sql = String.format("SELECT * FROM claims.%s WHERE id = ?::uuid", table);
        return executeQuery(SqlStatement.fromRaw(sql, List.of(id)));
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
